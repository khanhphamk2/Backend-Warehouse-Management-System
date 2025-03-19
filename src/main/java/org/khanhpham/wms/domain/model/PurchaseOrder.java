package org.khanhpham.wms.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.khanhpham.wms.common.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
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

    @Column(unique = true, nullable = false)
    private String poNumber;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false)
    private LocalDate receiveDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    private String notes;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private Set<PurchaseOrderItem> purchaseOrderItems;
}

