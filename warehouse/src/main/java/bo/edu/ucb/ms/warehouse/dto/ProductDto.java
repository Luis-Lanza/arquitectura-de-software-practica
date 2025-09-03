package bo.edu.ucb.ms.warehouse.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * DTO class for Product with only mandatory fields
 * Based on the product table schema
 */
public class ProductDto {

    @NotNull(message = "Product ID is required")
    private Integer id;

    @NotBlank(message = "Product name is required")
    private String name;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be greater than or equal to 0")
    private BigDecimal price;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
    private Integer stockQuantity;
    
    // Default constructor
    public ProductDto() {}
    
    // Constructor with required fields
    public ProductDto(String name, BigDecimal price, Integer stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
    
    // Getters and Setters

    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    @Override
    public String toString() {
        return "ProductDto{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
}