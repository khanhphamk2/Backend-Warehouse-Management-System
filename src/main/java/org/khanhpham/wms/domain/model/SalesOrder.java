package org.khanhpham.wms.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "sales_orders")
public class SalesOrder extends AudiEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Instant orderDate;
    private String status; // PENDING, SHIPPED, DELIVERED

    @OneToMany(mappedBy = "salesOrder")
    private List<SalesOrderItem> orderItems;
}
