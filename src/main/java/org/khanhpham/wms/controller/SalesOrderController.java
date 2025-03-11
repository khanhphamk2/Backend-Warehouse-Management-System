package org.khanhpham.wms.controller;

import lombok.RequiredArgsConstructor;
import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.dto.SalesOrderDTO;
import org.khanhpham.wms.domain.request.OrderStatusRequest;
import org.khanhpham.wms.domain.request.SalesOrderRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.service.SalesOrderService;
import org.khanhpham.wms.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("${spring.data.rest.base-path}/sales-orders")
public class SalesOrderController {
    private final SalesOrderService salesOrderService;

    @PostMapping("/process")
    public ResponseEntity<SalesOrderDTO> processSalesOrder(@RequestBody SalesOrderRequest request) {
        SalesOrderDTO createdOrder = salesOrderService.processSalesOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<SalesOrderDTO> getSalesOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(salesOrderService.getSalesOrder(orderId));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<PaginationResponse<SalesOrderDTO>> getSalesOrdersByCustomerId(
            @PathVariable Long supplierId,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir)
    {
        return ResponseEntity.ok(salesOrderService.getSalesOrdersByCustomerId(supplierId, pageNumber, pageSize, sortBy, sortDir));
    }

    @GetMapping("/all")
    public ResponseEntity<PaginationResponse<SalesOrderDTO>> getSalesOrders(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return ResponseEntity.ok(salesOrderService.getAllSalesOrders(pageNumber, pageSize, sortBy, sortDir));
    }

    @GetMapping("/by-status")
    public ResponseEntity<PaginationResponse<SalesOrderDTO>> getSalesOrdersByStatus(
            @RequestParam(value = "status", required = false) OrderStatus status,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir)
    {
        return ResponseEntity.ok(salesOrderService.findByStatus(status, pageNumber, pageSize, sortBy, sortDir));
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<PaginationResponse<SalesOrderDTO>> getSalesOrdersByDateRange(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir)
    {
        return ResponseEntity.ok(salesOrderService.findByDateRange(startDate, endDate, pageNumber, pageSize, sortBy, sortDir));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<SalesOrderDTO> changeOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusRequest request) {
        return ResponseEntity.ok(salesOrderService.updateOrderStatus(orderId, request));
    }
}
