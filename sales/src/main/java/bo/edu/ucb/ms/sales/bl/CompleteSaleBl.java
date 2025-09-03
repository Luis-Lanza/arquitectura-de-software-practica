package bo.edu.ucb.ms.sales.bl;

import bo.edu.ucb.ms.sales.client.AccountingClient;
import bo.edu.ucb.ms.sales.client.WarehouseClient;
import bo.edu.ucb.ms.sales.dto.ProductDto;
import bo.edu.ucb.ms.sales.dto.JournalDto;
import bo.edu.ucb.ms.sales.entity.Sale;
import bo.edu.ucb.ms.sales.repository.SaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CompleteSaleBl {

    private static final Logger logger = LoggerFactory.getLogger(CompleteSaleBl.class);

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private WarehouseClient warehouseClient;

    @Autowired
    private AccountingClient accountingClient;

    @Transactional(rollbackFor = Exception.class)
    public Sale createAndSaveSale(ProductDto productDto, Integer quantity) {
        logger.info("=== SALES SERVICE - SAGA ORCHESTRATOR ===");
        logger.info("CompleteSaleBl.createAndSaveSale called with productDto: {} and quantity: {}", 
                   productDto, quantity);

        String saleNumber = generateSaleNumber();
        logger.info("Generated sale number: {}", saleNumber);

        try {
            // STEP 1: Validate Product and Check Stock (Warehouse Service)
            logger.info("SAGA STEP 1: Validating product and checking stock");
            ProductDto validatedProduct = validateProductAndStock(productDto.getId(), quantity);
            logger.info("Product validated: {}", validatedProduct);

            // STEP 2: Reserve Stock (Warehouse Service)
            logger.info("SAGA STEP 2: Reserving stock");
            ProductDto updatedProduct = reserveStock(productDto.getId(), quantity);
            logger.info("Stock reserved: {}", updatedProduct);

            // STEP 3: Create Sale Entity (Sales Service)
            logger.info("SAGA STEP 3: Creating sale entity");
            Sale sale = createSaleEntity(validatedProduct, quantity, saleNumber);
            logger.info("Sale entity created: {}", sale);

            // STEP 4: Register Accounting Entries (Accounting Service)
            logger.info("SAGA STEP 4: Registering accounting entries");
            
            // Check for rollback trigger (0.99 price)
            if (validatedProduct.getPrice().compareTo(new BigDecimal("0.99")) == 0) {
                logger.warn("ROLLBACK TRIGGER DETECTED: Price is 0.99, forcing accounting failure for testing");
                throw new RuntimeException("Accounting service failure simulation (price = 0.99)");
            }
            
            try {
                registerSaleInJournal(sale);
                logger.info("Accounting entries registered successfully");
            } catch (Exception accountingException) {
                logger.warn("Accounting service not available, proceeding without accounting entries");
                logger.warn("This is expected when Accounting Service is not running");
                // For now, continue without accounting when service is not available
                // In production, this would require different handling based on business rules
            }

            // STEP 5: Save Sale (Sales Service)
            logger.info("SAGA STEP 5: Saving sale to database");
            Sale savedSale = saleRepository.save(sale);
            logger.info("Sale saved successfully: {}", savedSale);

            logger.info("=== SAGA COMPLETED SUCCESSFULLY ===");
            return savedSale;

        } catch (Exception e) {
            logger.error("=== SAGA FAILED - INITIATING ROLLBACK ===", e);
            
            // Rollback compensation: Release reserved stock
            try {
                logger.info("SAGA COMPENSATION: Releasing reserved stock for product: {}", productDto.getId());
                warehouseClient.releaseStock(productDto.getId(), quantity);
                logger.info("Stock released successfully during rollback");
            } catch (Exception rollbackException) {
                logger.error("CRITICAL: Failed to release stock during rollback", rollbackException);
            }

            // Rollback compensation: Delete any created accounting entries (if service is available)
            try {
                logger.info("SAGA COMPENSATION: Deleting accounting entries for transaction: {}", saleNumber);
                accountingClient.deleteJournalEntriesByTransaction(saleNumber);
                logger.info("Accounting entries deleted successfully during rollback");
            } catch (Exception rollbackException) {
                logger.warn("Accounting service not available for rollback compensation");
                // This is expected when Accounting Service is not running
            }

            logger.error("=== SAGA ROLLBACK COMPLETED ===");
            throw new RuntimeException("Sale creation failed: " + e.getMessage(), e);
        }
    }

    private ProductDto validateProductAndStock(Integer productId, Integer quantity) {
        logger.info("Validating product {} and checking stock for quantity: {}", productId, quantity);

        try {
            // Get product information
            ResponseEntity<ProductDto> productResponse = warehouseClient.getProduct(productId);
            if (!productResponse.getStatusCode().is2xxSuccessful() || productResponse.getBody() == null) {
                throw new RuntimeException("Product not found with ID: " + productId);
            }

            ProductDto product = productResponse.getBody();
            logger.info("Product retrieved: {}", product);

            // Check stock availability
            ResponseEntity<Map<String, Object>> stockResponse = 
                warehouseClient.checkStockAvailability(productId, quantity);
            
            if (!stockResponse.getStatusCode().is2xxSuccessful() || stockResponse.getBody() == null) {
                throw new RuntimeException("Unable to check stock availability for product: " + productId);
            }

            Map<String, Object> stockInfo = stockResponse.getBody();
            Boolean hasAvailableStock = (Boolean) stockInfo.get("hasAvailableStock");
            
            if (!hasAvailableStock) {
                Integer currentStock = (Integer) stockInfo.get("currentStock");
                throw new RuntimeException(String.format(
                    "Insufficient stock. Required: %d, Available: %d", quantity, currentStock));
            }

            logger.info("Stock availability confirmed for product: {}", productId);
            return product;

        } catch (Exception e) {
            logger.error("Failed to validate product and stock", e);
            throw new RuntimeException("Product validation failed: " + e.getMessage(), e);
        }
    }

    private ProductDto reserveStock(Integer productId, Integer quantity) {
        logger.info("Reserving stock for product: {} quantity: {}", productId, quantity);

        try {
            ResponseEntity<ProductDto> response = warehouseClient.reserveStock(productId, quantity);
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Failed to reserve stock for product: " + productId);
            }

            ProductDto updatedProduct = response.getBody();
            logger.info("Stock reserved successfully: {}", updatedProduct);
            return updatedProduct;

        } catch (Exception e) {
            logger.error("Failed to reserve stock", e);
            throw new RuntimeException("Stock reservation failed: " + e.getMessage(), e);
        }
    }

    private Sale createSaleEntity(ProductDto product, Integer quantity, String saleNumber) {
        logger.info("Creating sale entity for product: {} quantity: {} saleNumber: {}", 
                   product.getId(), quantity, saleNumber);

        Sale sale = new Sale();
        sale.setSaleNumber(saleNumber);
        sale.setProductId(product.getId());
        sale.setQuantity(quantity);
        sale.setUnitPrice(product.getPrice());
        sale.setPaymentStatus("pending");

        // Calculate total amount
        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        sale.setTotalAmount(totalAmount);

        logger.info("Sale entity prepared: {}", sale);
        return sale;
    }

    private void registerSaleInJournal(Sale sale) {
        logger.info("Registering sale in accounting journal for sale: {}", sale.getSaleNumber());

        try {
            List<JournalDto> journalEntries = createJournalEntries(sale);
            logger.info("Created {} journal entries for sale: {}", journalEntries.size(), sale.getSaleNumber());

            ResponseEntity<List<JournalDto>> response = accountingClient.createJournalEntries(journalEntries);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to create journal entries");
            }

            logger.info("Journal entries registered successfully");

        } catch (Exception e) {
            logger.error("Failed to register sale in journal", e);
            throw new RuntimeException("Accounting registration failed: " + e.getMessage(), e);
        }
    }

    private List<JournalDto> createJournalEntries(Sale sale) {
        List<JournalDto> entries = new ArrayList<>();

        // Debit: Accounts Receivable
        JournalDto debitEntry = new JournalDto();
        debitEntry.setAccountCode("1200");
        debitEntry.setAccountName("Accounts Receivable");
        debitEntry.setBalanceType("D");
        debitEntry.setAmount(sale.getTotalAmount());
        debitEntry.setDescription("Sale of products");
        debitEntry.setReferenceNumber(sale.getSaleNumber());
        debitEntry.setCreatedBy("SALES_SERVICE");
        entries.add(debitEntry);

        // Credit: Sales Revenue
        JournalDto creditEntry = new JournalDto();
        creditEntry.setAccountCode("4100");
        creditEntry.setAccountName("Sales Revenue");
        creditEntry.setBalanceType("C");
        creditEntry.setAmount(sale.getTotalAmount());
        creditEntry.setDescription("Sale of products");
        creditEntry.setReferenceNumber(sale.getSaleNumber());
        creditEntry.setCreatedBy("SALES_SERVICE");
        entries.add(creditEntry);

        return entries;
    }

    private ProductDto validateProductAndReserveStock(Integer productId, Integer quantity) {
        logger.info("Validating product {} and attempting to reserve {} units", productId, quantity);

        try {
            // Get product information to verify it exists
            ResponseEntity<ProductDto> productResponse = warehouseClient.getProduct(productId);
            if (!productResponse.getStatusCode().is2xxSuccessful() || productResponse.getBody() == null) {
                throw new RuntimeException("Product not found with ID: " + productId);
            }

            ProductDto product = productResponse.getBody();
            logger.info("Product exists: {}", product);

            // Attempt to reserve stock directly (this will validate availability atomically)
            ResponseEntity<ProductDto> reserveResponse = warehouseClient.reserveStock(productId, quantity);
            
            if (!reserveResponse.getStatusCode().is2xxSuccessful() || reserveResponse.getBody() == null) {
                throw new RuntimeException("Failed to reserve stock for product: " + productId);
            }

            ProductDto updatedProduct = reserveResponse.getBody();
            logger.info("Stock reserved successfully: {}", updatedProduct);
            return updatedProduct;

        } catch (Exception e) {
            logger.error("Failed to validate product and reserve stock", e);
            if (e.getMessage().contains("Insufficient stock") || e.getMessage().contains("[409]")) {
                throw new RuntimeException("Insufficient stock. Required: " + quantity + ", Available: 0", e);
            }
            throw new RuntimeException("Product validation and stock reservation failed: " + e.getMessage(), e);
        }
    }

    private String generateSaleNumber() {
        return "SALE-" + System.currentTimeMillis();
    }

    @Transactional(readOnly = true)
    public Sale getSaleByNumber(String saleNumber) {
        logger.info("=== SALES SERVICE ===");
        logger.info("CompleteSaleBl.getSaleByNumber called with saleNumber: {}", saleNumber);

        return saleRepository.findBySaleNumber(saleNumber).orElse(null);
    }

    @Transactional(readOnly = true)
    public Sale getSaleById(Integer saleId) {
        logger.info("=== SALES SERVICE ===");
        logger.info("CompleteSaleBl.getSaleById called with saleId: {}", saleId);

        return saleRepository.findById(saleId.longValue()).orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    public Sale createAndSaveSaleWithProductDtoPrice(ProductDto productDto, Integer quantity) {
        logger.info("=== SALES SERVICE - SAGA ORCHESTRATOR (MONOLITH-STYLE) ===");
        logger.info("CompleteSaleBl.createAndSaveSaleWithProductDtoPrice called with productDto: {} and quantity: {}", 
                   productDto, quantity);

        String saleNumber = generateSaleNumber();
        logger.info("Generated sale number: {}", saleNumber);

        try {
            // STEP 1: Validate Product exists and Reserve Stock (Warehouse Service) - atomic operation
            logger.info("SAGA STEP 1: Validating product exists and reserving stock");
            ProductDto validatedProduct = validateProductAndReserveStock(productDto.getId(), quantity);
            logger.info("Product validated and stock reserved for productId: {}", productDto.getId());

            // STEP 2: Create Sale Entity using ProductDto price (like monolith)
            logger.info("SAGA STEP 2: Creating sale entity using ProductDto price: {}", productDto.getPrice());
            Sale sale = createSaleEntity(productDto, quantity, saleNumber);
            logger.info("Sale entity created: {}", sale);

            // STEP 3: Register Accounting Entries (will trigger 0.99 rollback if needed)
            logger.info("SAGA STEP 3: Registering accounting entries");
            
            // Check for rollback trigger (0.99 price)
            if (productDto.getPrice().compareTo(new BigDecimal("0.99")) == 0) {
                logger.warn("ROLLBACK TRIGGER DETECTED: Price is 0.99, this may trigger accounting failure");
            }
            
            try {
                registerSaleInJournal(sale);
                logger.info("Accounting entries registered successfully");
            } catch (Exception accountingException) {
                logger.warn("Accounting service error: {}", accountingException.getMessage());
                // Re-throw to trigger rollback
                throw accountingException;
            }

            // STEP 4: Save Sale (Sales Service)
            logger.info("SAGA STEP 4: Saving sale to database");
            Sale savedSale = saleRepository.save(sale);
            logger.info("Sale saved successfully: {}", savedSale);

            logger.info("=== SAGA COMPLETED SUCCESSFULLY ===");
            return savedSale;

        } catch (Exception e) {
            logger.error("=== SAGA FAILED - INITIATING ROLLBACK ===", e);
            
            // Rollback compensation: Release reserved stock
            try {
                logger.info("SAGA COMPENSATION: Releasing reserved stock for product: {}", productDto.getId());
                warehouseClient.releaseStock(productDto.getId(), quantity);
                logger.info("Stock released successfully during rollback");
            } catch (Exception rollbackException) {
                logger.error("CRITICAL: Failed to release stock during rollback", rollbackException);
            }

            // Rollback compensation: Delete any created accounting entries (if service is available)
            try {
                logger.info("SAGA COMPENSATION: Deleting accounting entries for transaction: {}", saleNumber);
                accountingClient.deleteJournalEntriesByTransaction(saleNumber);
                logger.info("Accounting entries deleted successfully during rollback");
            } catch (Exception rollbackException) {
                logger.warn("Accounting service not available for rollback compensation");
            }

            logger.error("=== SAGA ROLLBACK COMPLETED ===");
            throw new RuntimeException("Sale creation failed: " + e.getMessage(), e);
        }
    }
}