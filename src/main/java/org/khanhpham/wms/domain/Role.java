package org.khanhpham.wms.domain;

import jakarta.persistence.*;
import lombok.*;
import org.khanhpham.wms.domain.model.AudiEntity;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "roles")
public class Role extends AudiEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;
}
