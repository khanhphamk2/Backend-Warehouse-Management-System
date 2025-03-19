package org.khanhpham.wms.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.khanhpham.wms.common.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "sales_orders")
public class SalesOrder extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String soNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false)
    private LocalDate orderDate;

    private LocalDate expectedShipmentDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal subtotal;

    private BigDecimal taxAmount;

    private BigDecimal shippingCost;

    private BigDecimal discount;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    private String notes;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private Set<SalesOrderItem> salesOrderItems;
}
