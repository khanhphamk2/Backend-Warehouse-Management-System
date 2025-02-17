package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}