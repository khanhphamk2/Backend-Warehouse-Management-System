package org.khanhpham.whs.domain.model;

import jakarta.persistence.*;
import jakarta.transaction.Transaction;
import lombok.*;
import org.khanhpham.whs.common.UserRole;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@RequiredArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User extends AudiEntity {
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
