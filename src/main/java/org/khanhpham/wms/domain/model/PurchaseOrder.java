package org.khanhpham.wms.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "purchase_orders")
public class PurchaseOrder extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private Instant orderDate;

    private Instant receiveDate;

    private String status;

    @OneToMany(mappedBy = "purchaseOrder")
    private Set<PurchaseOrderItem> orderItems;
}
