package org.khanhpham.wms.domain.mapper;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.khanhpham.wms.domain.dto.SupplierDTO;
import org.khanhpham.wms.domain.entity.Supplier;
import org.khanhpham.wms.domain.request.SupplierRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@AllArgsConstructor
public class SupplierMapper {
    private ModelMapper modelMapper;

    public SupplierDTO convertToDTO(Supplier supplier) {
        return modelMapper.map(supplier, SupplierDTO.class);
    }

    public Supplier convertToEntity(SupplierRequest request) {
        return Supplier.builder()
                .name(StringUtils.trim(request.getName()))
                .contactInfo(StringUtils.trim(request.getContactInfo()))
                .address(StringUtils.trim(request.getAddress()))
                .phone(StringUtils.trim(request.getPhone()))
                .email(StringUtils.trim(request.getEmail()))
                .description(StringUtils.trim(request.getDescription()))
                .purchaseOrders(new ArrayList<>())
                .build();
    }
}
