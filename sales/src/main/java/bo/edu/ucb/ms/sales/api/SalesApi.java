package bo.edu.ucb.ms.sales.api;

import bo.edu.ucb.ms.sales.bl.CompleteSaleBl;
import bo.edu.ucb.ms.sales.dto.ProductDto;
import bo.edu.ucb.ms.sales.dto.SaleDto;
import bo.edu.ucb.ms.sales.entity.Sale;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
public class SalesApi {

    private static final Logger logger = LoggerFactory.getLogger(SalesApi.class);

    @Autowired
    private CompleteSaleBl completeSaleBl;

    @PostMapping
    public ResponseEntity<SaleDto> createSale(@RequestBody @Valid ProductDto productDto, 
                                             @RequestParam Integer quantity) {
        logger.info("=== SALES API ===");
        logger.info("POST /api/sales called with productDto: {} and quantity: {}", productDto, quantity);

        try {
            if (quantity == null || quantity <= 0) {
                logger.warn("Invalid quantity provided: {}", quantity);
                return ResponseEntity.badRequest().build();
            }

            // Execute SAGA orchestration
            Sale createdSale = completeSaleBl.createAndSaveSale(productDto, quantity);
            
            // Convert to DTO
            SaleDto saleDto = convertToDto(createdSale);
            
            logger.info("Sale created successfully: {}", saleDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saleDto);

        } catch (RuntimeException e) {
            logger.error("Error creating sale", e);
            
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Insufficient stock")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else if (e.getMessage().contains("Accounting service failure simulation")) {
                // This is the 0.99 price rollback trigger
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
            } else if (e.getMessage().contains("Load balancer does not contain an instance")) {
                // Service unavailable (like Accounting Service not running)
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            logger.error("Unexpected error creating sale", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{saleId}")
    public ResponseEntity<SaleDto> getSale(@PathVariable Integer saleId) {
        logger.info("=== SALES API ===");
        logger.info("GET /api/sales/{} called", saleId);

        try {
            Sale sale = completeSaleBl.getSaleById(saleId);
            
            if (sale == null) {
                logger.warn("Sale not found with id: {}", saleId);
                return ResponseEntity.notFound().build();
            }

            SaleDto saleDto = convertToDto(sale);
            logger.info("Returning sale: {}", saleDto);
            return ResponseEntity.ok(saleDto);

        } catch (Exception e) {
            logger.error("Error retrieving sale with id: {}", saleId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/number/{saleNumber}")
    public ResponseEntity<SaleDto> getSaleByNumber(@PathVariable String saleNumber) {
        logger.info("=== SALES API ===");
        logger.info("GET /api/sales/number/{} called", saleNumber);

        try {
            Sale sale = completeSaleBl.getSaleByNumber(saleNumber);
            
            if (sale == null) {
                logger.warn("Sale not found with number: {}", saleNumber);
                return ResponseEntity.notFound().build();
            }

            SaleDto saleDto = convertToDto(sale);
            logger.info("Returning sale: {}", saleDto);
            return ResponseEntity.ok(saleDto);

        } catch (Exception e) {
            logger.error("Error retrieving sale with number: {}", saleNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/test")
    public ResponseEntity<SaleDto> createTestSale(@RequestParam(defaultValue = "1") Integer productId,
                                                 @RequestParam(defaultValue = "1") Integer quantity,
                                                 @RequestParam(defaultValue = "10.99") String price) {
        logger.info("=== SALES API - TEST ENDPOINT ===");
        logger.info("POST /api/sales/test called with productId: {}, quantity: {}, price: {}", 
                   productId, quantity, price);

        try {
            // Create test product DTO
            ProductDto testProduct = new ProductDto();
            testProduct.setId(productId);
            testProduct.setName("Test Product");
            testProduct.setPrice(java.math.BigDecimal.valueOf(Double.parseDouble(price)));
            testProduct.setStockQuantity(100); // Assume sufficient stock for test

            return createSale(testProduct, quantity);

        } catch (Exception e) {
            logger.error("Error in test sale creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private SaleDto convertToDto(Sale sale) {
        SaleDto dto = new SaleDto();
        dto.setId(sale.getId());
        dto.setSaleNumber(sale.getSaleNumber());
        dto.setProductId(sale.getProductId());
        dto.setQuantity(sale.getQuantity());
        dto.setUnitPrice(sale.getUnitPrice());
        dto.setDiscountPercentage(sale.getDiscountPercentage());
        dto.setDiscountAmount(sale.getDiscountAmount());
        dto.setTotalAmount(sale.getTotalAmount());
        dto.setTaxAmount(sale.getTaxAmount());
        dto.setFinalAmount(sale.getFinalAmount());
        dto.setCustomerName(sale.getCustomerName());
        dto.setCustomerEmail(sale.getCustomerEmail());
        dto.setPaymentStatus(sale.getPaymentStatus());
        dto.setNotes(sale.getNotes());
        dto.setCreatedAt(sale.getCreatedAt());
        dto.setUpdatedAt(sale.getUpdatedAt());
        return dto;
    }
}