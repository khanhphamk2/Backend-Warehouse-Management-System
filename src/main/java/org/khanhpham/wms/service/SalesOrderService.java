package org.khanhpham.wms.service;

import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.dto.SalesOrderDTO;
import org.khanhpham.wms.domain.request.OrderStatusRequest;
import org.khanhpham.wms.domain.request.SalesOrderRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;

import java.time.LocalDate;

public interface SalesOrderService {
    PaginationResponse<SalesOrderDTO> getAllSalesOrders(int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginationResponse<SalesOrderDTO>  findByStatus(OrderStatus status, int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginationResponse<SalesOrderDTO> findByDateRange(LocalDate startDate, LocalDate endDate, int pageNumber, int pageSize, String sortBy, String sortDir);
    SalesOrderDTO updateOrderStatus(Long id, OrderStatusRequest request);
    SalesOrderDTO getSalesOrder(Long orderId);
    PaginationResponse<SalesOrderDTO> getSalesOrdersByCustomerId(Long customerId, int pageNumber, int pageSize, String sortBy, String sortDir);
    SalesOrderDTO processSalesOrder(SalesOrderRequest request);
}
