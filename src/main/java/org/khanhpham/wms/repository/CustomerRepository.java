package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}