package bo.edu.ucb.ms.accounting.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal")
public class Journal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "journal_entry_number", unique = true, nullable = false, length = 20)
    private String journalEntryNumber;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;
    
    @Column(name = "posting_date")
    private LocalDate postingDate;
    
    @Column(name = "account_code", nullable = false, length = 20)
    private String accountCode;
    
    @Column(name = "account_name", nullable = false, length = 255)
    private String accountName;
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "reference_number", length = 50)
    private String referenceNumber;
    
    @Column(name = "debit_amount", precision = 15, scale = 2)
    private BigDecimal debitAmount = BigDecimal.ZERO;
    
    @Column(name = "credit_amount", precision = 15, scale = 2)
    private BigDecimal creditAmount = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "balance_type", length = 1)
    private BalanceType balanceType;
    
    @Column(name = "department", length = 100)
    private String department;
    
    @Column(name = "cost_center", length = 50)
    private String costCenter;
    
    @Column(name = "project_code", length = 50)
    private String projectCode;
    
    @Column(name = "currency_code", length = 3)
    private String currencyCode = "USD";
    
    @Column(name = "exchange_rate", precision = 10, scale = 6)
    private BigDecimal exchangeRate = BigDecimal.ONE;
    
    @Column(name = "source_document", length = 100)
    private String sourceDocument;
    
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;
    
    @Column(name = "approved_by", length = 100)
    private String approvedBy;
    
    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status = Status.draft;
    
    @Column(name = "reversed_by_entry", length = 20)
    private String reversedByEntry;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum BalanceType {
        D, C
    }
    
    public enum Status {
        draft, posted, reversed
    }
    
    // Constructors
    public Journal() {}
    
    public Journal(String journalEntryNumber, LocalDate transactionDate, String accountCode, 
                   String accountName, String description, String createdBy) {
        this.journalEntryNumber = journalEntryNumber;
        this.transactionDate = transactionDate;
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.description = description;
        this.createdBy = createdBy;
        this.postingDate = LocalDate.now();
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (postingDate == null) {
            postingDate = LocalDate.now();
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
    
    public String getJournalEntryNumber() {
        return journalEntryNumber;
    }
    
    public void setJournalEntryNumber(String journalEntryNumber) {
        this.journalEntryNumber = journalEntryNumber;
    }
    
    public LocalDate getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public LocalDate getPostingDate() {
        return postingDate;
    }
    
    public void setPostingDate(LocalDate postingDate) {
        this.postingDate = postingDate;
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
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
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
    
    public BalanceType getBalanceType() {
        return balanceType;
    }
    
    public void setBalanceType(BalanceType balanceType) {
        this.balanceType = balanceType;
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
    
    public String getProjectCode() {
        return projectCode;
    }
    
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }
    
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }
    
    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
    
    public String getSourceDocument() {
        return sourceDocument;
    }
    
    public void setSourceDocument(String sourceDocument) {
        this.sourceDocument = sourceDocument;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }
    
    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getReversedByEntry() {
        return reversedByEntry;
    }
    
    public void setReversedByEntry(String reversedByEntry) {
        this.reversedByEntry = reversedByEntry;
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
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business methods (from monolith)
    public boolean isBalanced() {
        return debitAmount.compareTo(creditAmount) == 0;
    }
    
    public boolean isPosted() {
        return status == Status.posted;
    }
    
    public boolean isReversed() {
        return status == Status.reversed;
    }
    
    public void approve(String approvedBy) {
        this.approvedBy = approvedBy;
        this.approvalDate = LocalDateTime.now();
    }
    
    public void post() {
        if (this.status == Status.draft) {
            this.status = Status.posted;
        } else {
            throw new IllegalStateException("Only draft entries can be posted");
        }
    }
    
    public void reverse(String reversedByEntry) {
        if (this.status == Status.posted) {
            this.status = Status.reversed;
            this.reversedByEntry = reversedByEntry;
        } else {
            throw new IllegalStateException("Only posted entries can be reversed");
        }
    }
    
    // MICROSERVICES-ONLY METHOD (NOT IN MONOLITH) - For DTO compatibility
    public BigDecimal getAmount() {
        if (debitAmount != null && debitAmount.compareTo(BigDecimal.ZERO) > 0) {
            return debitAmount;
        }
        if (creditAmount != null && creditAmount.compareTo(BigDecimal.ZERO) > 0) {
            return creditAmount;
        }
        return BigDecimal.ZERO;
    }
    
    // MICROSERVICES-ONLY METHOD (NOT IN MONOLITH) - For DTO compatibility  
    public String getBalanceTypeAsString() {
        return balanceType != null ? balanceType.name() : null;
    }
    
    @Override
    public String toString() {
        return "Journal{" +
                "id=" + id +
                ", journalEntryNumber='" + journalEntryNumber + '\'' +
                ", transactionDate=" + transactionDate +
                ", accountCode='" + accountCode + '\'' +
                ", accountName='" + accountName + '\'' +
                ", debitAmount=" + debitAmount +
                ", creditAmount=" + creditAmount +
                ", status=" + status +
                '}';
    }
}