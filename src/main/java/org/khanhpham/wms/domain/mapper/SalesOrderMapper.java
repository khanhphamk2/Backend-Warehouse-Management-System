package org.khanhpham.wms.domain.mapper;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.dto.SalesOrderDTO;
import org.khanhpham.wms.domain.dto.ShortProductDTO;
import org.khanhpham.wms.domain.entity.Customer;
import org.khanhpham.wms.domain.entity.SalesOrder;
import org.khanhpham.wms.domain.request.SalesOrderRequest;
import org.khanhpham.wms.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class SalesOrderMapper {
    private ModelMapper modelMapper;
    private final CustomerService customerService;

    public @NotNull SalesOrderDTO convertToDTO(SalesOrder salesOrder) {
        SalesOrderDTO salesOrderDTO = modelMapper.map(salesOrder, SalesOrderDTO.class);

        var salesOrderItems = salesOrder.getSalesOrderItems();

        if (salesOrderItems != null && !salesOrderItems.isEmpty()) {
            List<ShortProductDTO> items = salesOrderItems.stream()
                    .map(item -> {
                        var product = item.getProduct();
                        return modelMapper.map(product, ShortProductDTO.class);
                    })
                    .toList();

            salesOrderDTO.setProducts(items);
        } else {
            salesOrderDTO.setProducts(Collections.emptyList());
        }

        return salesOrderDTO;
    }

    public SalesOrder convertToEntity(@NotNull SalesOrderRequest request) {
        Customer customer = customerService.findById(request.getCustomerId());

        return SalesOrder.builder()
                .customer(customer)
                .orderDate(request.getOrderDate())
                .expectedShipmentDate(request.getExpectedShipmentDate())
                .status(OrderStatus.PROCESSING)
                .subtotal(request.getSubtotal())
                .shippingCost(request.getShippingCost())
                .taxAmount(request.getTaxAmount())
                .discount(request.getDiscount())
                .totalAmount(BigDecimal.ZERO)
                .notes(!request.getNotes().isBlank() ? request.getNotes() : "")
                .build();
    }
}
