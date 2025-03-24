package org.khanhpham.wms.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.dto.OrderItemDTO;
import org.khanhpham.wms.domain.dto.SalesOrderDTO;
import org.khanhpham.wms.domain.entity.*;
import org.khanhpham.wms.domain.mapper.SalesOrderMapper;
import org.khanhpham.wms.domain.request.OrderStatusRequest;
import org.khanhpham.wms.domain.request.SalesOrderRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.SalesOrderRepository;
import org.khanhpham.wms.service.ProductService;
import org.khanhpham.wms.service.SalesOrderService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.khanhpham.wms.utils.TrackingNumberGenerator;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderMapper soMapper;
    private final ProductService productService;

    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = initializeValidTransitions();

    @Override
    public PaginationResponse<SalesOrderDTO> getAllSalesOrders(
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<SalesOrder> salesOrders = salesOrderRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize,sortBy, sortDir)
        );

        List<SalesOrderDTO> salesOrderDTOS = salesOrders.getContent()
                .stream()
                .map(soMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(salesOrderDTOS, salesOrders);
    }

    @Override
    public PaginationResponse<SalesOrderDTO> findByStatus(
            OrderStatus status, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<SalesOrder> salesOrders = salesOrderRepository.findByStatus(
                status,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<SalesOrderDTO> salesOrderDTOS = salesOrders.getContent()
                .stream()
                .map(soMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(salesOrderDTOS, salesOrders);
    }

    @Override
    public PaginationResponse<SalesOrderDTO> findByDateRange(
            LocalDate startDate, LocalDate endDate,
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<SalesOrder> salesOrders = salesOrderRepository.findByOrderDateBetween(
                startDate,
                endDate,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<SalesOrderDTO> salesOrderDTOS = salesOrders.getContent()
                .stream()
                .map(soMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(salesOrderDTOS, salesOrders);
    }

    @Override
    public SalesOrderDTO updateOrderStatus(Long id, OrderStatusRequest request) {
        if (id == null || request == null || request.getStatus() == null) {
            throw new IllegalArgumentException("Order ID and status cannot be null");
        }

        SalesOrder order = findById(id);

        OrderStatus newStatus = request.getStatus();

        if (order.getStatus() != newStatus) {
            validateStatusTransition(order.getStatus(), newStatus);

            order.setStatus(newStatus);

            return soMapper.convertToDTO(salesOrderRepository.save(order));
        }

        return soMapper.convertToDTO(order);
    }

    @Override
    public SalesOrderDTO getSalesOrder(Long id) {
        return soMapper.convertToDTO(findById(id));
    }

    @Override
    public PaginationResponse<SalesOrderDTO> getSalesOrdersByCustomerId(
            Long customerId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<SalesOrder> salesOrders = salesOrderRepository.findByCustomerId(
                customerId,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<SalesOrderDTO> salesOrderDTOS = salesOrders.getContent()
                .stream()
                .map(soMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(salesOrderDTOS, salesOrders);
    }

    @Override
    public SalesOrderDTO processSalesOrder(SalesOrderRequest request) {
        SalesOrder salesOrder = createSalesOrder(request);
        salesOrder.setTotalAmount(calculateTotalFromItems(request.getProducts()));
        updateProductQuantity(salesOrder);

        return soMapper.convertToDTO(salesOrder);
    }

    private SalesOrder findById(Long id) {
        return salesOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sales Order", "id", id));
    }

    private @NotNull SalesOrder createSalesOrder(SalesOrderRequest request) {
        SalesOrder salesOrder = soMapper.convertToEntity(request);

        salesOrder.setSoNumber(TrackingNumberGenerator.generateSalesOrderNumber());

        Set<SalesOrderItem> salesOrderItems = createSalesOrderItems(salesOrder, request.getProducts());

        salesOrder.setSalesOrderItems(salesOrderItems);

        return salesOrderRepository.save(salesOrder);
    }

    private @NotNull Set<SalesOrderItem> createSalesOrderItems(SalesOrder salesOrder,
                                                               @NotNull List<OrderItemDTO> products) {
        Set<SalesOrderItem> salesOrderItems = new HashSet<>();
        for (OrderItemDTO item : products) {
            Product product = productService.findById(item.getProductId());

            SalesOrderItem salesOrderItem = SalesOrderItem.builder()
                    .salesOrder(salesOrder)
                    .product(product)
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .notes(item.getNotes())
                    .build();

            salesOrderItems.add(salesOrderItem);
        }
        return salesOrderItems;
    }

    private void updateProductQuantity(@NotNull SalesOrder salesOrder) {
        for (SalesOrderItem item : salesOrder.getSalesOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productService.save(product);
        }
    }

    private BigDecimal calculateTotalFromItems(@NotNull List<OrderItemDTO> items) {
        return items.stream()
                .map(item -> item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private static @NotNull @UnmodifiableView Map<OrderStatus, Set<OrderStatus>> initializeValidTransitions() {
        Map<OrderStatus, Set<OrderStatus>> transitions = new EnumMap<>(OrderStatus.class);

        transitions.put(OrderStatus.PROCESSING, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED));
        transitions.put(OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED));
        transitions.put(OrderStatus.DELIVERED, EnumSet.of(OrderStatus.COMPLETED, OrderStatus.RETURNED));

        transitions.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
        transitions.put(OrderStatus.RETURNED, EnumSet.noneOf(OrderStatus.class));
        transitions.put(OrderStatus.COMPLETED, EnumSet.noneOf(OrderStatus.class));

        return Collections.unmodifiableMap(transitions);
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            return;
        }

        Set<OrderStatus> validNextStatuses = VALID_TRANSITIONS.get(currentStatus);

        if (validNextStatuses == null || !validNextStatuses.contains(newStatus)) {
            throw new IllegalStateException(
                    String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
            );
        }
    }
}
