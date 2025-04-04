package org.khanhpham.wms.service;

import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.dto.PurchaseOrderDTO;
import org.khanhpham.wms.domain.request.OrderStatusRequest;
import org.khanhpham.wms.domain.request.PurchaseOrderRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;

import java.time.LocalDate;

public interface PurchaseOrderService {
    PaginationResponse<PurchaseOrderDTO>  findByStatus(OrderStatus status, int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginationResponse<PurchaseOrderDTO> findByDateRange(LocalDate startDate, LocalDate endDate, int pageNumber, int pageSize, String sortBy, String sortDir);
    PurchaseOrderDTO updateOrderStatus(Long id, OrderStatusRequest request);
    PurchaseOrderDTO getPurchaseOrder(Long id);
    PaginationResponse<PurchaseOrderDTO> getPurchaseOrdersBySupplierId(Long supplierId, int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginationResponse<PurchaseOrderDTO> getAllPurchaseOrders(int pageNumber, int pageSize, String sortBy, String sortDir);
    PurchaseOrderDTO processPurchaseOrder(PurchaseOrderRequest request);
}
