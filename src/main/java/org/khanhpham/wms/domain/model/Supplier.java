package org.khanhpham.wms.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "suppliers")
public class Supplier extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String contactInfo;
    private String address;
    private String phone;
    private String email;
    private String description;
    @OneToMany(mappedBy = "supplier")
    private List<PurchaseOrder> purchaseOrders;
}
