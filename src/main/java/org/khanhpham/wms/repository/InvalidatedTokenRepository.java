package org.khanhpham.wms.repository;

import org.khanhpham.wms.domain.model.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, Long> {
    boolean existsById(String token);
}