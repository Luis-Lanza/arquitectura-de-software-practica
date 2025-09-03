package bo.edu.ucb.ms.sales.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Sale")
public class Sale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "sale_number", length = 20, unique = true, nullable = false)
    private String saleNumber;
    
    @Column(name = "product_id", nullable = false)
    private Integer productId;
    
    @Column(nullable = false)
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;
    
    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Unit price must be greater than or equal to 0")
    private BigDecimal unitPrice;
    
    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Discount percentage must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Discount percentage must be between 0 and 100")
    private BigDecimal discountPercentage = BigDecimal.ZERO;
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "final_amount", precision = 12, scale = 2, insertable = false, updatable = false)
    private BigDecimal finalAmount;
    
    @Column(name = "sale_date", nullable = false)
    private LocalDate saleDate = LocalDate.now();
    
    @Column(name = "customer_id")
    private Integer customerId;
    
    @Column(name = "customer_name", length = 255)
    private String customerName;
    
    @Column(length = 100)
    private String salesperson;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod = "cash";
    
    @Column(name = "payment_status", length = 20)
    @Pattern(regexp = "pending|paid|partial|cancelled", message = "Payment status must be one of: pending, paid, partial, cancelled")
    private String paymentStatus = "pending";
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Default constructor
    public Sale() {}
    
    // Constructor with required fields
    public Sale(String saleNumber, Integer productId, Integer quantity, BigDecimal unitPrice) {
        this.saleNumber = saleNumber;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (saleDate == null) {
            saleDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSaleNumber() {
        return saleNumber;
    }
    
    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }
    
    public Integer getProductId() {
        return productId;
    }
    
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getFinalAmount() {
        return finalAmount;
    }
    
    public LocalDate getSaleDate() {
        return saleDate;
    }
    
    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }
    
    public Integer getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getSalesperson() {
        return salesperson;
    }
    
    public void setSalesperson(String salesperson) {
        this.salesperson = salesperson;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", saleNumber='" + saleNumber + '\'' +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalAmount=" + totalAmount +
                ", discountPercentage=" + discountPercentage +
                ", discountAmount=" + discountAmount +
                ", finalAmount=" + finalAmount +
                ", saleDate=" + saleDate +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", salesperson='" + salesperson + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}