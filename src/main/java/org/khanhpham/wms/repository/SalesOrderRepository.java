package org.khanhpham.wms.repository;

import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.entity.SalesOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    Page<SalesOrder> findByStatus(OrderStatus status, Pageable pageable);
    Page<SalesOrder> findByOrderDateBetween(LocalDate begin, LocalDate end, Pageable pageable);
    Page<SalesOrder> findByCustomerId(Long customerId, Pageable pageable);
}