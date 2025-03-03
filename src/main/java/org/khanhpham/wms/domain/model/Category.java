package org.khanhpham.wms.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "categories")
public class Category extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @ManyToMany(mappedBy = "categories")
    private Set<Product> products;
}
