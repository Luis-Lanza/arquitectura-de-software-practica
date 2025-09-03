package bo.edu.ucb.ms.accounting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class JournalDto {
    
    private String accountCode;
    private String accountName;
    private String description;
    private BigDecimal amount;
    private String balanceType; // "D" for Debit, "C" for Credit
    private LocalDate transactionDate;
    private String referenceNumber;
    private String createdBy;
    private String department;
    private String costCenter;
    private String notes;
    
    // Constructors
    public JournalDto() {}
    
    public JournalDto(String accountCode, String accountName, String description, 
                      BigDecimal amount, String balanceType, String createdBy) {
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.description = description;
        this.amount = amount;
        this.balanceType = balanceType;
        this.createdBy = createdBy;
        this.transactionDate = LocalDate.now();
    }
    
    // Getters and Setters
    public String getAccountCode() {
        return accountCode;
    }
    
    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }
    
    public String getAccountName() {
        return accountName;
    }
    
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getBalanceType() {
        return balanceType;
    }
    
    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }
    
    public LocalDate getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getCostCenter() {
        return costCenter;
    }
    
    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "JournalDto{" +
                "accountCode='" + accountCode + '\'' +
                ", accountName='" + accountName + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", balanceType='" + balanceType + '\'' +
                ", transactionDate=" + transactionDate +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}