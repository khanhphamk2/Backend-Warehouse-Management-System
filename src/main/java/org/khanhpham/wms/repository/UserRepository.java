package org.khanhpham.whs.repository;

import org.khanhpham.whs.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}