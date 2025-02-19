package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhone(String phone);
    Optional<Customer> findByName(String name);
    Optional<Customer> findByEmailOrPhone(String email, String phone);
}