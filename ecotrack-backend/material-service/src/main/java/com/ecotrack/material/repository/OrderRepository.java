package com.ecotrack.material.repository;

import com.ecotrack.material.entity.Order;
import com.ecotrack.material.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByIndustryId(Long industryId, Pageable pageable);

    Page<Order> findByIndustryIdAndOrderStatus(Long industryId, OrderStatus status, Pageable pageable);

    Page<Order> findByRecyclerId(Long recyclerId, Pageable pageable);

    Page<Order> findByRecyclerIdAndOrderStatus(Long recyclerId, OrderStatus status, Pageable pageable);

    Page<Order> findByOrderStatus(OrderStatus status, Pageable pageable);
}
