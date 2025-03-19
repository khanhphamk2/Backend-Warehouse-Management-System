package org.khanhpham.wms.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "sales_order_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "purchase_order_id"})
})
public class SalesOrderItem extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    private String notes;

    @PrePersist
    @PreUpdate
    protected void calculateTotalPrice() {
        if (unitPrice != null && quantity > 0) {
            totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            totalPrice = BigDecimal.ZERO;
        }
    }
}
