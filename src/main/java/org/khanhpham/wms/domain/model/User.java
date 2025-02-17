package org.khanhpham.wms.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.khanhpham.wms.common.UserRole;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

//    @Column(name = "address", nullable = false)
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    @ToString.Exclude
//    private List<Transaction> transactions;

    @Column(name = "role", nullable = false)
    private UserRole role;
}
