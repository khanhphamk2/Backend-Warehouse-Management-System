package org.khanhpham.wms.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "sales_order_items")
public class SalesOrderItem extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private long quantity;
    private double unitPrice;
}
