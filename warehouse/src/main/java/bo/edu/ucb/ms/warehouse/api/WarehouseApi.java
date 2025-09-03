package bo.edu.ucb.ms.warehouse.api;

import bo.edu.ucb.ms.warehouse.bl.ProductStockBl;
import bo.edu.ucb.ms.warehouse.dto.ProductDto;
import bo.edu.ucb.ms.warehouse.entity.Product;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseApi {

    private static final Logger logger = LoggerFactory.getLogger(WarehouseApi.class);

    @Autowired
    private ProductStockBl productStockBl;

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Integer productId) {
        logger.info("=== WAREHOUSE API ===");
        logger.info("GET /api/warehouse/products/{} called", productId);

        try {
            Product product = productStockBl.getProductById(productId);
            
            if (product == null) {
                logger.warn("Product not found with id: {}", productId);
                return ResponseEntity.notFound().build();
            }

            ProductDto productDto = new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity()
            );

            logger.info("Returning product: {}", productDto);
            return ResponseEntity.ok(productDto);

        } catch (Exception e) {
            logger.error("Error getting product with id: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/products/{productId}/stock/check")
    public ResponseEntity<Map<String, Object>> checkStockAvailability(
            @PathVariable Integer productId,
            @RequestParam Integer requiredQuantity) {
        
        logger.info("=== WAREHOUSE API ===");
        logger.info("POST /api/warehouse/products/{}/stock/check called with quantity: {}", 
                   productId, requiredQuantity);

        try {
            boolean hasStock = productStockBl.hasAvailableStock(productId, requiredQuantity);
            Product product = productStockBl.getProductById(productId);

            Map<String, Object> response = new HashMap<>();
            response.put("productId", productId);
            response.put("requiredQuantity", requiredQuantity);
            response.put("hasAvailableStock", hasStock);
            
            if (product != null) {
                response.put("currentStock", product.getStockQuantity());
            }

            logger.info("Stock availability check result: {}", response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error checking stock availability for product: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/products/{productId}/stock/reserve")
    public ResponseEntity<ProductDto> reserveStock(
            @PathVariable Integer productId,
            @RequestParam Integer quantity) {
        
        logger.info("=== WAREHOUSE API ===");
        logger.info("POST /api/warehouse/products/{}/stock/reserve called with quantity: {}", 
                   productId, quantity);

        try {
            Product updatedProduct = productStockBl.reserveStock(productId, quantity);
            
            ProductDto productDto = new ProductDto(
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getPrice(),
                updatedProduct.getStockQuantity()
            );

            logger.info("Stock reserved successfully: {}", productDto);
            return ResponseEntity.ok(productDto);

        } catch (IllegalStateException e) {
            logger.warn("Insufficient stock for reservation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid parameters for stock reservation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error reserving stock for product: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/products/{productId}/stock/release")
    public ResponseEntity<ProductDto> releaseStock(
            @PathVariable Integer productId,
            @RequestParam Integer quantity) {
        
        logger.info("=== WAREHOUSE API ===");
        logger.info("POST /api/warehouse/products/{}/stock/release called with quantity: {}", 
                   productId, quantity);

        try {
            Product updatedProduct = productStockBl.releaseStock(productId, quantity);
            
            ProductDto productDto = new ProductDto(
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getPrice(),
                updatedProduct.getStockQuantity()
            );

            logger.info("Stock released successfully: {}", productDto);
            return ResponseEntity.ok(productDto);

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid parameters for stock release: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error releasing stock for product: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/products/{productId}/stock")
    public ResponseEntity<ProductDto> updateStock(
            @PathVariable Integer productId,
            @RequestBody @Valid ProductDto productDto) {
        
        logger.info("=== WAREHOUSE API ===");
        logger.info("PUT /api/warehouse/products/{}/stock called with data: {}", productId, productDto);

        try {
            Product product = productStockBl.getProductById(productId);
            if (product == null) {
                logger.warn("Product not found for stock update: {}", productId);
                return ResponseEntity.notFound().build();
            }

            product.setStockQuantity(productDto.getStockQuantity());
            Product updatedProduct = productStockBl.updateProductStock(product);
            
            ProductDto responseDto = new ProductDto(
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getPrice(),
                updatedProduct.getStockQuantity()
            );

            logger.info("Stock updated successfully: {}", responseDto);
            return ResponseEntity.ok(responseDto);

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid parameters for stock update: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating stock for product: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}