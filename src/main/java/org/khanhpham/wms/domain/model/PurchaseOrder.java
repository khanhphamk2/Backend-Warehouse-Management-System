package org.khanhpham.wms.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.khanhpham.wms.common.OrderStatus;

import java.math.BigDecimal;
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

    @Column(nullable = false)
    private Instant orderDate;

    @Column(nullable = false)
    private Instant receiveDate;

    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PROCESSING;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "purchaseOrder")
    private Set<PurchaseOrderItem> orderItems;
}
