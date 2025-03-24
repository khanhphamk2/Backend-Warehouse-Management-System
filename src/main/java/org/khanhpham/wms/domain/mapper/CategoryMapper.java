package org.khanhpham.wms.domain.mapper;

import lombok.AllArgsConstructor;
import org.khanhpham.wms.domain.dto.CategoryDTO;
import org.khanhpham.wms.domain.entity.Category;
import org.khanhpham.wms.domain.request.CategoryRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CategoryMapper {
    private final ModelMapper modelMapper;

    public CategoryDTO convertToDTO(Category category) {
        return modelMapper.map(category, CategoryDTO.class);
    }

    public Category convertToEntity(CategoryRequest categoryRequest) {
        return Category.builder()
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .build();
    }
}
