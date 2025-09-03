package bo.edu.ucb.ms.warehouse.bl;

import bo.edu.ucb.ms.warehouse.entity.Product;
import bo.edu.ucb.ms.warehouse.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductStockBl {

    private static final Logger logger = LoggerFactory.getLogger(ProductStockBl.class);

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Product getProductById(Integer productId) {
        logger.info("=== WAREHOUSE SERVICE ===");
        logger.info("ProductStockBl.getProductById called with productId: {}", productId);
        
        if (productId == null) {
            logger.warn("ProductId is null");
            return null;
        }

        Product product = productRepository.findById(productId).orElse(null);
        
        if (product != null) {
            logger.info("Product found: {}", product);
        } else {
            logger.warn("Product not found with id: {}", productId);
        }

        return product;
    }

    @Transactional
    public Product updateProductStock(Product product) {
        logger.info("=== WAREHOUSE SERVICE ===");
        logger.info("ProductStockBl.updateProductStock called with product: {}", product);

        if (product == null) {
            logger.error("Product is null");
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (product.getId() == null) {
            logger.error("Product ID is null");
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        if (product.getStockQuantity() < 0) {
            logger.error("Stock quantity is negative: {}", product.getStockQuantity());
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        Product existingProduct = productRepository.findById(product.getId()).orElse(null);
        if (existingProduct == null) {
            logger.error("Product not found with id: {}", product.getId());
            throw new IllegalArgumentException("Product not found with id: " + product.getId());
        }

        existingProduct.setStockQuantity(product.getStockQuantity());
        Product savedProduct = productRepository.save(existingProduct);
        
        logger.info("Product stock updated successfully: {}", savedProduct);
        return savedProduct;
    }

    @Transactional(readOnly = true)
    public boolean hasAvailableStock(Integer productId, Integer requiredQuantity) {
        logger.info("=== WAREHOUSE SERVICE ===");
        logger.info("ProductStockBl.hasAvailableStock called with productId: {} and requiredQuantity: {}", 
                   productId, requiredQuantity);

        if (productId == null || requiredQuantity == null || requiredQuantity <= 0) {
            logger.warn("Invalid parameters: productId={}, requiredQuantity={}", productId, requiredQuantity);
            return false;
        }

        Product product = getProductById(productId);
        if (product == null) {
            logger.warn("Product not found for stock validation");
            return false;
        }

        boolean hasStock = product.getStockQuantity() >= requiredQuantity;
        logger.info("Stock availability check result: {} (current stock: {}, required: {})", 
                   hasStock, product.getStockQuantity(), requiredQuantity);
        
        return hasStock;
    }

    @Transactional
    public Product reserveStock(Integer productId, Integer quantity) {
        logger.info("=== WAREHOUSE SERVICE ===");
        logger.info("ProductStockBl.reserveStock called with productId: {} and quantity: {}", 
                   productId, quantity);

        if (!hasAvailableStock(productId, quantity)) {
            logger.error("Insufficient stock for product id: {}, required: {}", productId, quantity);
            throw new IllegalStateException("Insufficient stock available");
        }

        Product product = getProductById(productId);
        product.setStockQuantity(product.getStockQuantity() - quantity);
        
        Product updatedProduct = updateProductStock(product);
        logger.info("Stock reserved successfully for product: {}", updatedProduct);
        
        return updatedProduct;
    }

    @Transactional
    public Product releaseStock(Integer productId, Integer quantity) {
        logger.info("=== WAREHOUSE SERVICE ===");
        logger.info("ProductStockBl.releaseStock called with productId: {} and quantity: {}", 
                   productId, quantity);

        Product product = getProductById(productId);
        if (product == null) {
            logger.error("Cannot release stock for non-existent product: {}", productId);
            throw new IllegalArgumentException("Product not found");
        }

        product.setStockQuantity(product.getStockQuantity() + quantity);
        
        Product updatedProduct = updateProductStock(product);
        logger.info("Stock released successfully for product: {}", updatedProduct);
        
        return updatedProduct;
    }
}