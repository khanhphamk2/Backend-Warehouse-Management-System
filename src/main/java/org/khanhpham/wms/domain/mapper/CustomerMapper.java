package org.khanhpham.wms.domain.mapper;

import lombok.AllArgsConstructor;
import org.khanhpham.wms.domain.dto.CustomerDTO;
import org.khanhpham.wms.domain.entity.Customer;
import org.khanhpham.wms.domain.request.CustomerRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomerMapper {
    private ModelMapper modelMapper;

    public CustomerDTO convertToDTO(Object object) {
        return modelMapper.map(object, CustomerDTO.class);
    }

    public Customer convertToEntity(CustomerRequest customerRequest) {
        return Customer.builder()
                .address(customerRequest.getAddress())
                .email(customerRequest.getEmail())
                .name(customerRequest.getName())
                .phone(customerRequest.getPhone())
                .build();
    }
}
