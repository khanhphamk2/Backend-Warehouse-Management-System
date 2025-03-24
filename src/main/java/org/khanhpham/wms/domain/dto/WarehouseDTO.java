package org.khanhpham.wms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link org.khanhpham.wms.domain.entity.Warehouse}
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WarehouseDTO extends AuditDTO implements Serializable {
    Long id;
    String name;
    String location;
    Long managerId;
    String address;
    String warehouseCode;
    String description;
}