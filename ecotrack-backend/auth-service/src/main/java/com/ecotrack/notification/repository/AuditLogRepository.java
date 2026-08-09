package com.ecotrack.notification.repository;

import com.ecotrack.notification.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:role IS NULL OR LOWER(COALESCE(a.userRole, '')) LIKE LOWER(CONCAT('%', :role, '%'))) AND " +
            "(:region IS NULL OR LOWER(COALESCE(a.region, '')) LIKE LOWER(CONCAT('%', :region, '%')) OR LOWER(COALESCE(a.details, '')) LIKE LOWER(CONCAT('%', :region, '%'))) AND " +
            "(:status IS NULL OR a.responseStatus = :status OR (:status = 200 AND a.responseStatus IS NULL)) AND " +
            "(:search IS NULL OR " +
            "LOWER(COALESCE(a.action, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(COALESCE(a.url, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(COALESCE(a.details, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(COALESCE(a.method, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(COALESCE(a.region, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(COALESCE(a.requestId, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(COALESCE(a.userRole, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(COALESCE(a.ipAddress, '')) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<AuditLog> searchAuditLogs(
            @Param("role") String role,
            @Param("region") String region,
            @Param("status") Integer status,
            @Param("search") String search,
            Pageable pageable);
}
