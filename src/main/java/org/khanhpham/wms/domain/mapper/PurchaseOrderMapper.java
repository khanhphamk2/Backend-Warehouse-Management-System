package org.khanhpham.wms.domain.mapper;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.dto.PurchaseOrderDTO;
import org.khanhpham.wms.domain.dto.ShortProductDTO;
import org.khanhpham.wms.domain.entity.PurchaseOrder;
import org.khanhpham.wms.domain.entity.Supplier;
import org.khanhpham.wms.domain.request.PurchaseOrderRequest;
import org.khanhpham.wms.service.SupplierService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class PurchaseOrderMapper {
    private final ModelMapper modelMapper;
    private final SupplierService supplierService;

    public PurchaseOrderDTO convertToDTO(PurchaseOrder purchaseOrder) {
        PurchaseOrderDTO purchaseOrderDTO = modelMapper.map(purchaseOrder, PurchaseOrderDTO.class);

        var purchaseOrderItems = purchaseOrder.getPurchaseOrderItems();

        if (purchaseOrderItems != null && !purchaseOrderItems.isEmpty()) {
            List<ShortProductDTO> items = purchaseOrderItems.stream()
                    .map(item -> {
                        var product = item.getProduct();
                        return modelMapper.map(product, ShortProductDTO.class);
                    })
                    .toList();

            purchaseOrderDTO.setProducts(items);
        } else {
            purchaseOrderDTO.setProducts(Collections.emptyList());
        }

        return purchaseOrderDTO;
    }

    public PurchaseOrder convertToEntity(@NotNull PurchaseOrderRequest request) {
        Supplier supplier = supplierService.findById(request.getSupplierId());

        return PurchaseOrder.builder()
                .supplier(supplier)
                .orderDate(request.getOrderDate())
                .receiveDate(request.getReceiveDate())
                .totalAmount(BigDecimal.ZERO)
                .notes(!request.getNotes().isEmpty() ? request.getNotes() : "")
                .status(OrderStatus.PROCESSING)
                .build();
    }
}
