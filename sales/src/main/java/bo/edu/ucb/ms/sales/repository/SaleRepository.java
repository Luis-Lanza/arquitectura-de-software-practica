package bo.edu.ucb.ms.sales.repository;

import bo.edu.ucb.ms.sales.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findBySaleNumber(String saleNumber);

    List<Sale> findByCustomerName(String customerName);

    List<Sale> findByProductId(Integer productId);

    List<Sale> findByPaymentStatus(String paymentStatus);

    List<Sale> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT s FROM Sale s WHERE s.totalAmount >= :minAmount AND s.totalAmount <= :maxAmount")
    List<Sale> findByTotalAmountBetween(@Param("minAmount") BigDecimal minAmount, 
                                       @Param("maxAmount") BigDecimal maxAmount);

    @Query("SELECT s FROM Sale s WHERE s.createdAt >= :startDate AND s.paymentStatus = :status")
    List<Sale> findByCreatedAtAfterAndPaymentStatus(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("status") String status);

    @Query("SELECT s FROM Sale s WHERE s.productId = :productId AND s.createdAt BETWEEN :startDate AND :endDate")
    List<Sale> findByProductIdAndDateRange(@Param("productId") Integer productId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(s.totalAmount) FROM Sale s WHERE s.paymentStatus = 'paid' AND s.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalSalesAmount(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    Long countSalesByDateRange(@Param("startDate") LocalDateTime startDate, 
                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT s.paymentStatus FROM Sale s")
    List<String> findDistinctPaymentStatuses();
}