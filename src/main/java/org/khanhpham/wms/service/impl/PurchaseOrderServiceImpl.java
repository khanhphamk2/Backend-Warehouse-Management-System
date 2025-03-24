package org.khanhpham.wms.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.dto.OrderItemDTO;
import org.khanhpham.wms.domain.dto.PurchaseOrderDTO;
import org.khanhpham.wms.domain.entity.Product;
import org.khanhpham.wms.domain.entity.PurchaseOrder;
import org.khanhpham.wms.domain.entity.PurchaseOrderItem;
import org.khanhpham.wms.domain.mapper.PurchaseOrderMapper;
import org.khanhpham.wms.domain.request.OrderStatusRequest;
import org.khanhpham.wms.domain.request.PurchaseOrderRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.repository.PurchaseOrderRepository;
import org.khanhpham.wms.service.ProductService;
import org.khanhpham.wms.service.PurchaseOrderService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.khanhpham.wms.utils.TrackingNumberGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderMapper poMapper;
    private final ProductService productService;

    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = initializeValidTransitions();

    @Override
    public PurchaseOrderDTO processPurchaseOrder(PurchaseOrderRequest request) {
        PurchaseOrder purchaseOrder = createPurchaseOrder(request);
        purchaseOrder.setTotalAmount(calculateTotalFromItems(request.getProducts()));
        updateProductQuantity(purchaseOrder);

        return poMapper.convertToDTO(purchaseOrder);
    }

    @Override
    public PurchaseOrderDTO updateOrderStatus(Long id, OrderStatusRequest request) {
        if (id == null || request == null || request.getStatus() == null) {
            throw new IllegalArgumentException("Order ID and status cannot be null");
        }

        PurchaseOrder order = findById(id);

        OrderStatus newStatus = request.getStatus();

        if (order.getStatus() != newStatus) {
            validateStatusTransition(order.getStatus(), newStatus);

            order.setStatus(newStatus);

            return poMapper.convertToDTO(purchaseOrderRepository.save(order));
        }

        return poMapper.convertToDTO(order);
    }

    @Override
    public PurchaseOrderDTO getPurchaseOrder(Long id) {
        return poMapper.convertToDTO(findById(id));
    }

    @Override
    public PaginationResponse<PurchaseOrderDTO> getPurchaseOrdersBySupplierId(Long supplierId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir);
        Page<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findBySupplierId(supplierId, pageable);

        List<PurchaseOrderDTO> purchaseOrderDTOs = purchaseOrders.getContent()
                .stream()
                .map(poMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(purchaseOrderDTOs, purchaseOrders);
    }

    @Override
    public PaginationResponse<PurchaseOrderDTO> getAllPurchaseOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<PurchaseOrderDTO> purchaseOrderDTOs = purchaseOrders.getContent()
                .stream()
                .map(poMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(purchaseOrderDTOs, purchaseOrders);
    }

    @Override
    public PaginationResponse<PurchaseOrderDTO> findByDateRange(LocalDate startDate, LocalDate endDate, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByOrderDateBetween(
                startDate,
                endDate,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<PurchaseOrderDTO> purchaseOrderDTOs = purchaseOrders.getContent()
                .stream()
                .map(poMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(purchaseOrderDTOs, purchaseOrders);
    }

    @Override
    public PaginationResponse<PurchaseOrderDTO> findByStatus(OrderStatus status, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByStatus(
                status,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<PurchaseOrderDTO> purchaseOrderDTOs = purchaseOrders.getContent()
                .stream()
                .map(poMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(purchaseOrderDTOs, purchaseOrders);
    }

    private PurchaseOrder findById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with id: " + id));
    }

    private @NotNull PurchaseOrder createPurchaseOrder(PurchaseOrderRequest request) {
        PurchaseOrder purchaseOrder = poMapper.convertToEntity(request);

        purchaseOrder.setPoNumber(TrackingNumberGenerator.generatePurchaseOrderNumber());

        Set<PurchaseOrderItem> purchaseOrderItems = createPurchaseOrderItems(purchaseOrder, request.getProducts());

        purchaseOrder.setPurchaseOrderItems(purchaseOrderItems);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    private @NotNull Set<PurchaseOrderItem> createPurchaseOrderItems(PurchaseOrder purchaseOrder,
                                                                     @NotNull List<OrderItemDTO> products) {
        Set<PurchaseOrderItem> purchaseOrderItems = new HashSet<>();
        for (OrderItemDTO item : products) {
            Product product = productService.findById(item.getProductId());

            PurchaseOrderItem purchaseOrderItem = PurchaseOrderItem.builder()
                    .purchaseOrder(purchaseOrder)
                    .product(product)
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .notes(item.getNotes())
                    .build();

            purchaseOrderItems.add(purchaseOrderItem);
        }
        return purchaseOrderItems;
    }

    private void updateProductQuantity(@NotNull PurchaseOrder purchaseOrder) {
        for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
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
