package org.khanhpham.wms.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.dto.ProductPurchaseDTO;
import org.khanhpham.wms.domain.dto.PurchaseOrderDTO;
import org.khanhpham.wms.domain.dto.ShortProductDTO;
import org.khanhpham.wms.domain.model.Product;
import org.khanhpham.wms.domain.model.PurchaseOrder;
import org.khanhpham.wms.domain.model.PurchaseOrderItem;
import org.khanhpham.wms.domain.model.Supplier;
import org.khanhpham.wms.domain.request.PurchaseOrderRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.CustomException;
import org.khanhpham.wms.repository.ProductRepository;
import org.khanhpham.wms.repository.PurchaseOrderItemRepository;
import org.khanhpham.wms.repository.PurchaseOrderRepository;
import org.khanhpham.wms.repository.SupplierRepository;
import org.khanhpham.wms.service.ProductService;
import org.khanhpham.wms.service.PurchaseOrderService;
import org.khanhpham.wms.service.SupplierService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final ModelMapper modelMapper;
    private final ProductService productService;
    private final SupplierService supplierService;

    private PurchaseOrderDTO convertToDTO(PurchaseOrder purchaseOrder) {
        PurchaseOrderDTO purchaseOrderDTO = modelMapper.map(purchaseOrder, PurchaseOrderDTO.class);

        var purchaseOrderItems = purchaseOrder.getPurchaseOrderItems();
        List<ShortProductDTO> items = new ArrayList<>();
        for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItems) {
            ShortProductDTO shortProductDTO = modelMapper.map(purchaseOrderItem.getProduct(), ShortProductDTO.class);
            shortProductDTO.setProductId(purchaseOrderItem.getProduct().getId());
            shortProductDTO.setQuantity(purchaseOrderItem.getQuantity());
            shortProductDTO.setPrice(purchaseOrderItem.getPrice());

            if (purchaseOrderItem.getProduct().getImageUrl() != null && !purchaseOrderItem.getProduct().getImageUrl().isEmpty()) {
                shortProductDTO.setImageUrl(purchaseOrderItem.getProduct().getImageUrl());
            } else {
                shortProductDTO.setImageUrl("");
            }

            items.add(shortProductDTO);
        }

        purchaseOrderDTO.setProducts(items);

        return purchaseOrderDTO;
    }

    private PurchaseOrder convertToEntity(PurchaseOrderRequest request) {

        Supplier supplier = supplierService.getSupplierById(request.getSupplierId());

        return PurchaseOrder.builder()
                .supplier(supplier)
                .orderDate(request.getOrderDate())
                .receiveDate(request.getReceiveDate())
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.PROCESSING)
                .build();
    }

    @Override
    public PurchaseOrderDTO processPurchaseOrder(PurchaseOrderRequest request) {
        PurchaseOrder purchaseOrder = createPurchaseOrder(request);
        purchaseOrder.setTotalAmount(calculateTotalFromItems(request.getProducts()));
        updateProductQuantity(purchaseOrder, request.getProducts());

        return convertToDTO(purchaseOrder);
    }

    @Override
    public PurchaseOrderDTO updateOrderStatus(Long id, OrderStatus status) {
        PurchaseOrder order = findById(id);

        // Add business logic for status transitions
        validateStatusTransition(order.getStatus(), status);

        order.setStatus(status);
        return convertToDTO(purchaseOrderRepository.save(order));
    }

    @Override
    public PurchaseOrderDTO getPurchaseOrder(Long id) {
        return convertToDTO(findById(id));
    }

    @Override
    public PaginationResponse<PurchaseOrderDTO> getPurchaseOrdersBySupplierId(Long supplierId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir);
        Page<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findBySupplierId(supplierId, pageable);

        List<PurchaseOrderDTO> purchaseOrderDTOs = purchaseOrders.getContent()
                .stream()
                .map(this::convertToDTO)
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
                .map(this::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(purchaseOrderDTOs, purchaseOrders);
    }

    @Override
    public PaginationResponse<PurchaseOrderDTO> findByDateRange(Instant startDate, Instant endDate, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByOrderDateBetween(
                startDate,
                endDate,
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<PurchaseOrderDTO> purchaseOrderDTOs = purchaseOrders.getContent()
                .stream()
                .map(this::convertToDTO)
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
                .map(this::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(purchaseOrderDTOs, purchaseOrders);
    }

    private PurchaseOrder findById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with id: " + id));
    }

    private PurchaseOrder createPurchaseOrder(PurchaseOrderRequest request) {
        PurchaseOrder purchaseOrder = convertToEntity(request);

        Set<PurchaseOrderItem> purchaseOrderItems = createPurchaseOrderItems(purchaseOrder, request.getProducts());
        purchaseOrder.setPurchaseOrderItems(purchaseOrderItems);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    private Set<PurchaseOrderItem> createPurchaseOrderItems(PurchaseOrder purchaseOrder,
                                                            List<ProductPurchaseDTO> products) {
        Set<PurchaseOrderItem> purchaseOrderItems = new HashSet<>();
        for (ProductPurchaseDTO item : products) {
            Product product = productService.findById(item.getProductId());

            PurchaseOrderItem purchaseOrderItem = PurchaseOrderItem.builder()
                    .purchaseOrder(purchaseOrder)
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(item.getPurchasePrice())
                    .build();

            purchaseOrderItems.add(purchaseOrderItem);
        }
        return purchaseOrderItems;
    }

    private void updateProductQuantity(PurchaseOrder purchaseOrder, List<ProductPurchaseDTO> products) {
        for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productService.save(product);
        }
    }

    private void validatePurchaseOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getSupplier() == null || purchaseOrder.getSupplier().getId() == null) {
            throw new IllegalArgumentException("Supplier is required for purchase order");
        }

        if (supplierService.getSupplierById(purchaseOrder.getSupplier().getId()) == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Supplier not found with id: " + purchaseOrder.getSupplier().getId());
        }

        if (purchaseOrder.getReceiveDate() == null) {
            throw new IllegalArgumentException("Receive date is required for purchase order");
        }
    }

    private BigDecimal calculateTotalFromItems(List<ProductPurchaseDTO> items) {
        return items.stream()
                .map(item -> item.getPurchasePrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Implement business rules for status transitions
        switch (currentStatus) {
            case PROCESSING:
                // Can transition to SHIPPED or CANCELLED
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from PROCESSING to " + newStatus);
                }
                break;
            case SHIPPED:
                // Can only transition to DELIVERED or CANCELLED
                if (newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from SHIPPED to " + newStatus);
                }
                break;
            case DELIVERED:
                // Can transition to COMPLETED or RETURNED
                if (newStatus != OrderStatus.COMPLETED && newStatus != OrderStatus.RETURNED) {
                    throw new IllegalStateException("Invalid status transition from DELIVERED to " + newStatus);
                }
                break;
            case CANCELLED:
            case RETURNED:
            case COMPLETED:
                // Terminal states, no transitions allowed
                throw new IllegalStateException("Cannot change status from terminal state: " + currentStatus);
            default:
                throw new IllegalStateException("Unknown status: " + currentStatus);
        }
    }

}
