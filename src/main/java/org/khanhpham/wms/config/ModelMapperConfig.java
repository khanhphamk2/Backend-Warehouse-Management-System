package org.khanhpham.wms.config;

import org.khanhpham.wms.domain.dto.ProductDTO;
import org.khanhpham.wms.domain.model.Product;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        // Configure map Product to ProductDTO
        modelMapper.addMappings(new PropertyMap<Product, ProductDTO>() {
            @Override
            protected void configure() {
                map().setCreatedDate(source.getCreatedDate());
                map().setLastModifiedDate(source.getLastModifiedDate());
                map().setCreatedBy(source.getCreatedBy());
                map().setLastModifiedBy(source.getLastModifiedBy());
            }
        });

        return modelMapper;
    }
}
