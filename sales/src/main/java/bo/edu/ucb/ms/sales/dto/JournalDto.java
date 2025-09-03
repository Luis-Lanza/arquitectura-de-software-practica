package bo.edu.ucb.ms.sales.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class JournalDto {

    private Integer id;

    @NotBlank(message = "Transaction number is required")
    private String transactionNumber;

    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotBlank(message = "Transaction type is required")
    private String transactionType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String description;
    private String reference;
    private LocalDateTime createdAt;

    public JournalDto() {}

    public JournalDto(String transactionNumber, String accountName, String transactionType, BigDecimal amount) {
        this.transactionNumber = transactionNumber;
        this.accountName = accountName;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public JournalDto(String transactionNumber, String accountName, String transactionType, 
                     BigDecimal amount, String description, String reference) {
        this.transactionNumber = transactionNumber;
        this.accountName = accountName;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.reference = reference;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "JournalDto{" +
                "id=" + id +
                ", transactionNumber='" + transactionNumber + '\'' +
                ", accountName='" + accountName + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", reference='" + reference + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}