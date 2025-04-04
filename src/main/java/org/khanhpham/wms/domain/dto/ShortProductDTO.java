package org.khanhpham.wms.domain.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ShortProductDTO implements Serializable {
    private Long productId;
    private String name;
    private String sku;
    private String imageUrl;
    private int quantity;
    private BigDecimal price;
}
