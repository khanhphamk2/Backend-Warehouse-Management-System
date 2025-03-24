package org.khanhpham.wms.service;

import org.khanhpham.wms.domain.dto.UserDTO;
import org.khanhpham.wms.domain.entity.User;
import org.khanhpham.wms.domain.request.RegisterRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;

import java.util.Optional;

public interface UserService {
    UserDTO findByIdentity(String identity);
    UserDTO findById(Long id);
    UserDTO createUser(RegisterRequest registerRequest, String passwordEncoder);
    UserDTO findByUsernameOrEmail(String email, String username);
    Optional<User> findByEmail(String email);
    void changePassword(String username, String password);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameOrEmail(String identity);
    User getUserById(Long id);
    PaginationResponse<UserDTO> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);
}
