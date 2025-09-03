package bo.edu.ucb.ms.warehouse.bl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.ucb.ms.warehouse.entity.Product;
import bo.edu.ucb.ms.warehouse.repository.ProductRepository;

@Service
public class ProductStockBl {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Transactional
    public Product getProductById(Integer id) {
        System.out.println("=== ProductStockBl.getProductById ===");
        System.out.println("Buscando producto con ID: " + id);
        
        try {
            Product product = productRepository.findById(id).orElse(null);
            
            if (product != null) {
                System.out.println("Producto encontrado: " + product.getName() + 
                                 ", Stock: " + product.getStockQuantity() + 
                                 ", ID: " + product.getId());
            } else {
                System.out.println("Producto NO encontrado para ID: " + id);
            }
            
            return product;
        } catch (Exception e) {
            System.out.println("ERROR al buscar producto: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Updates the stock information of a product
     * @param product The product with updated stock information
     * @return The updated Product entity
     * @throws IllegalArgumentException if product is null or has invalid data
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Product updateProductStock(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        
        // Verify the product exists in the database
        if (!productRepository.existsById(product.getId())) {
            throw new IllegalArgumentException("Product with ID " + product.getId() + " not found");
        }
        
        // Validate stock quantity is not negative
        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        
        // Save and return the updated product
        return productRepository.save(product);
    }
    
    // MICROSERVICES-ONLY METHODS (NOT IN MONOLITH) - Required for SAGA pattern
    
    @Transactional(readOnly = true)
    public boolean hasAvailableStock(Integer productId, Integer requiredQuantity) {
        System.out.println("=== WAREHOUSE SERVICE - MICROSERVICES ONLY ===");
        System.out.println("ProductStockBl.hasAvailableStock called with productId: " + productId + " and requiredQuantity: " + requiredQuantity);

        if (productId == null || requiredQuantity == null || requiredQuantity <= 0) {
            System.out.println("Invalid parameters: productId=" + productId + ", requiredQuantity=" + requiredQuantity);
            return false;
        }

        Product product = getProductById(productId);
        if (product == null) {
            System.out.println("Product not found for stock validation");
            return false;
        }

        boolean hasStock = product.getStockQuantity() >= requiredQuantity;
        System.out.println("Stock availability check result: " + hasStock + " (current stock: " + product.getStockQuantity() + ", required: " + requiredQuantity + ")");
        
        return hasStock;
    }

    @Transactional
    public Product reserveStock(Integer productId, Integer quantity) {
        System.out.println("=== WAREHOUSE SERVICE - MICROSERVICES ONLY ===");
        System.out.println("ProductStockBl.reserveStock called with productId: " + productId + " and quantity: " + quantity);

        if (!hasAvailableStock(productId, quantity)) {
            System.out.println("ERROR: Insufficient stock for product id: " + productId + ", required: " + quantity);
            throw new IllegalStateException("Insufficient stock available");
        }

        Product product = getProductById(productId);
        product.setStockQuantity(product.getStockQuantity() - quantity);
        
        Product updatedProduct = updateProductStock(product);
        System.out.println("Stock reserved successfully for product: " + updatedProduct);
        
        return updatedProduct;
    }

    @Transactional
    public Product releaseStock(Integer productId, Integer quantity) {
        System.out.println("=== WAREHOUSE SERVICE - MICROSERVICES ONLY ===");
        System.out.println("ProductStockBl.releaseStock called with productId: " + productId + " and quantity: " + quantity);

        Product product = getProductById(productId);
        if (product == null) {
            System.out.println("ERROR: Cannot release stock for non-existent product: " + productId);
            throw new IllegalArgumentException("Product not found");
        }

        product.setStockQuantity(product.getStockQuantity() + quantity);
        
        Product updatedProduct = updateProductStock(product);
        System.out.println("Stock released successfully for product: " + updatedProduct);
        
        return updatedProduct;
    }
}