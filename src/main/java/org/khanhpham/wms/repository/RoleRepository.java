package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}