package org.khanhpham.wms.domain.mapper;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.khanhpham.wms.common.ProductStatus;
import org.khanhpham.wms.domain.dto.ProductDTO;
import org.khanhpham.wms.domain.entity.Category;
import org.khanhpham.wms.domain.entity.Product;
import org.khanhpham.wms.domain.entity.Supplier;
import org.khanhpham.wms.domain.request.ProductRequest;
import org.khanhpham.wms.service.CategoryService;
import org.khanhpham.wms.service.SupplierService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
@AllArgsConstructor
public class ProductMapper {
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private ModelMapper modelMapper;

    public ProductDTO convertToDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }

    public Product convertToEntity(@NotNull ProductRequest request) {
        Supplier supplier = supplierService.getSupplierById(request.getSupplierId());
        List<Category> categories = categoryService.getAllById(request.getCategoryIds());
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sku(request.getSku())
                .expiryDate(request.getExpiryDate())
                .unit(request.getUnit())
                .quantity(request.getQuantity())
                .imageUrl(request.getImageUrl())
                .supplier(supplier)
                .status(ProductStatus.ACTIVE)
                .categories(new HashSet<>(categories))
                .build();
    }

    public void map(ProductRequest request, @NotNull Product product) {
        modelMapper.map(request, product);
    }
}
