package com.anymind.pos.repository;

import com.anymind.pos.domain.Payment;
import com.anymind.pos.dto.SalesRecordResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {
    Optional<Payment> findPaymentByIdempotencyKey(String idempotencyKey);

    @Query("""
            SELECT new com.anymind.pos.dto.SalesRecordResponseDTO(
                FUNCTION('DATE_TRUNC', 'hour', p.datetime),
                SUM(p.finalPrice),
                SUM(p.points)
            )
            FROM Payment p
            WHERE p.datetime BETWEEN :startDateTime AND :endDateTime
            GROUP BY FUNCTION('DATE_TRUNC', 'hour', p.datetime)
            ORDER BY FUNCTION('DATE_TRUNC', 'hour', p.datetime)
            """)
    List<SalesRecordResponseDTO> findHourlySales(@Param("startDateTime") LocalDateTime startDateTime,
                                                 @Param("endDateTime") LocalDateTime endDateTime);
}
