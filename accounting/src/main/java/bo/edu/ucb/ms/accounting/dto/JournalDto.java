package bo.edu.ucb.ms.accounting.dto;

import bo.edu.ucb.ms.accounting.entity.BalanceType;
import bo.edu.ucb.ms.accounting.entity.Status;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class JournalDto {

    private Integer id;
    private String journalEntryNumber;

    @NotBlank(message = "Account code is required")
    private String accountCode;

    @NotBlank(message = "Account name is required")
    private String accountName;

    private String description;

    private LocalDate transactionDate;
    private String referenceNumber;

    @NotNull(message = "Balance type is required")
    private BalanceType balanceType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private String currency;
    private BigDecimal exchangeRate;
    private Status status;
    private String department;
    private String costCenter;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime postedAt;
    private String postedBy;
    private String notes;

    public JournalDto() {}

    public JournalDto(String accountCode, String accountName, String description, 
                     BalanceType balanceType, BigDecimal amount, String referenceNumber) {
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.description = description;
        this.balanceType = balanceType;
        this.amount = amount;
        this.referenceNumber = referenceNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJournalEntryNumber() {
        return journalEntryNumber;
    }

    public void setJournalEntryNumber(String journalEntryNumber) {
        this.journalEntryNumber = journalEntryNumber;
    }

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

    public BalanceType getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(BalanceType balanceType) {
        this.balanceType = balanceType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
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
                "id=" + id +
                ", journalEntryNumber='" + journalEntryNumber + '\'' +
                ", accountCode='" + accountCode + '\'' +
                ", accountName='" + accountName + '\'' +
                ", description='" + description + '\'' +
                ", transactionDate=" + transactionDate +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", balanceType=" + balanceType +
                ", amount=" + amount +
                ", debitAmount=" + debitAmount +
                ", creditAmount=" + creditAmount +
                ", currency='" + currency + '\'' +
                ", exchangeRate=" + exchangeRate +
                ", status=" + status +
                ", department='" + department + '\'' +
                ", costCenter='" + costCenter + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", postedAt=" + postedAt +
                ", postedBy='" + postedBy + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}