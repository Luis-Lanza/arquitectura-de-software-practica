package bo.edu.ucb.ms.accounting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal")
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Journal entry number is required")
    @Column(name = "journal_entry_number", nullable = false, unique = true, length = 50)
    private String journalEntryNumber;

    @NotBlank(message = "Account code is required")
    @Column(name = "account_code", nullable = false, length = 20)
    private String accountCode;

    @NotBlank(message = "Account name is required")
    @Column(name = "account_name", nullable = false, length = 255)
    private String accountName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Transaction date is required")
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @NotNull(message = "Balance type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "balance_type", nullable = false, length = 1)
    private BalanceType balanceType;

    @Column(name = "debit_amount", precision = 15, scale = 2)
    private BigDecimal debitAmount;

    @Column(name = "credit_amount", precision = 15, scale = 2)
    private BigDecimal creditAmount;

    @Column(length = 3, columnDefinition = "VARCHAR(3) DEFAULT 'USD'")
    private String currency = "USD";

    @Column(name = "exchange_rate", precision = 15, scale = 6, columnDefinition = "DECIMAL(15,6) DEFAULT 1.000000")
    private BigDecimal exchangeRate = BigDecimal.ONE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'draft'")
    private Status status = Status.draft;

    @Column(length = 100)
    private String department;

    @Column(name = "cost_center", length = 100)
    private String costCenter;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @Column(name = "posted_by", length = 100)
    private String postedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (transactionDate == null) {
            transactionDate = LocalDate.now();
        }
        if (journalEntryNumber == null) {
            generateJournalEntryNumber();
        }
        if (createdBy == null) {
            createdBy = "SYSTEM";
        }
        setAmountByBalanceType();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        setAmountByBalanceType();
    }

    private void generateJournalEntryNumber() {
        LocalDateTime now = LocalDateTime.now();
        // Include nanoseconds to ensure uniqueness even for simultaneous entries
        long nanos = now.getNano() / 1000000; // Convert to milliseconds (3 digits)
        this.journalEntryNumber = String.format("JE-%04d%02d%02d-%02d%02d%02d-%03d",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), now.getSecond(), nanos);
    }

    private void setAmountByBalanceType() {
        if (balanceType != null) {
            if (balanceType == BalanceType.D) {
                // For debit entries
                if (creditAmount != null && creditAmount.compareTo(BigDecimal.ZERO) > 0) {
                    debitAmount = creditAmount;
                    creditAmount = BigDecimal.ZERO;
                }
            } else if (balanceType == BalanceType.C) {
                // For credit entries
                if (debitAmount != null && debitAmount.compareTo(BigDecimal.ZERO) > 0) {
                    creditAmount = debitAmount;
                    debitAmount = BigDecimal.ZERO;
                }
            }
        }
    }

    public boolean isBalanced() {
        BigDecimal totalDebits = debitAmount != null ? debitAmount : BigDecimal.ZERO;
        BigDecimal totalCredits = creditAmount != null ? creditAmount : BigDecimal.ZERO;
        return totalDebits.compareTo(totalCredits) == 0;
    }

    public void post() {
        if (status == Status.draft) {
            status = Status.posted;
            postedAt = LocalDateTime.now();
            if (postedBy == null) {
                postedBy = createdBy;
            }
        }
    }

    public void reverse() {
        if (status == Status.posted) {
            status = Status.reversed;
        }
    }

    public BigDecimal getAmount() {
        if (debitAmount != null && debitAmount.compareTo(BigDecimal.ZERO) > 0) {
            return debitAmount;
        }
        if (creditAmount != null && creditAmount.compareTo(BigDecimal.ZERO) > 0) {
            return creditAmount;
        }
        return BigDecimal.ZERO;
    }

    public Journal() {}

    public Journal(String accountCode, String accountName, String description, 
                  BalanceType balanceType, BigDecimal amount, String referenceNumber) {
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.description = description;
        this.balanceType = balanceType;
        this.referenceNumber = referenceNumber;
        
        if (balanceType == BalanceType.D) {
            this.debitAmount = amount;
            this.creditAmount = BigDecimal.ZERO;
        } else {
            this.creditAmount = amount;
            this.debitAmount = BigDecimal.ZERO;
        }
    }

    // Getters and Setters
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
        return "Journal{" +
                "id=" + id +
                ", journalEntryNumber='" + journalEntryNumber + '\'' +
                ", accountCode='" + accountCode + '\'' +
                ", accountName='" + accountName + '\'' +
                ", description='" + description + '\'' +
                ", transactionDate=" + transactionDate +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", balanceType=" + balanceType +
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