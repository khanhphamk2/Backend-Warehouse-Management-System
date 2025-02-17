package org.khanhpham.wms.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "warehouses")
public class Warehouse extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;
    private String address;
    private String warehouseCode;
    private String description;

    @OneToMany(mappedBy = "warehouse")
    private List<InventoryItem> inventoryItems;
}
