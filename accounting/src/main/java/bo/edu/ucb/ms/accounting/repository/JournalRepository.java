package bo.edu.ucb.ms.accounting.repository;

import bo.edu.ucb.ms.accounting.entity.Journal;
import bo.edu.ucb.ms.accounting.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Integer> {

    Optional<Journal> findByJournalEntryNumber(String journalEntryNumber);

    List<Journal> findByReferenceNumber(String referenceNumber);

    List<Journal> findByAccountCode(String accountCode);

    List<Journal> findByAccountName(String accountName);

    List<Journal> findByStatus(Status status);

    List<Journal> findByTransactionDate(LocalDate transactionDate);

    List<Journal> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);

    List<Journal> findByCreatedBy(String createdBy);

    List<Journal> findByDepartment(String department);

    List<Journal> findByCostCenter(String costCenter);

    @Query("SELECT j FROM Journal j WHERE j.referenceNumber = :referenceNumber ORDER BY j.createdAt ASC")
    List<Journal> findByReferenceNumberOrderByCreatedAt(@Param("referenceNumber") String referenceNumber);

    @Query("SELECT j FROM Journal j WHERE j.debitAmount >= :minAmount OR j.creditAmount >= :minAmount")
    List<Journal> findByAmountGreaterThanOrEqual(@Param("minAmount") BigDecimal minAmount);

    @Query("SELECT j FROM Journal j WHERE j.status = :status AND j.transactionDate BETWEEN :startDate AND :endDate")
    List<Journal> findByStatusAndTransactionDateBetween(@Param("status") Status status,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(j.debitAmount) FROM Journal j WHERE j.status = 'posted' AND j.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumDebitAmountByDateRange(@Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(j.creditAmount) FROM Journal j WHERE j.status = 'posted' AND j.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumCreditAmountByDateRange(@Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(j) FROM Journal j WHERE j.status = :status")
    Long countByStatus(@Param("status") Status status);

    @Query("DELETE FROM Journal j WHERE j.referenceNumber = :referenceNumber")
    void deleteByReferenceNumber(@Param("referenceNumber") String referenceNumber);

    @Query("SELECT DISTINCT j.accountCode FROM Journal j ORDER BY j.accountCode")
    List<String> findDistinctAccountCodes();

    @Query("SELECT j FROM Journal j WHERE j.createdAt >= :startDateTime ORDER BY j.createdAt DESC")
    List<Journal> findRecentEntries(@Param("startDateTime") LocalDateTime startDateTime);
}