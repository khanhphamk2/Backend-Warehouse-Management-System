package org.khanhpham.wms.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "warehouses")
public class Warehouse extends AudiEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private String manager;
    private String address;
    private String warehouseCode;
    private String description;
    @OneToMany(mappedBy = "warehouse")
    private List<InventoryItem> inventoryItems;
}
