package org.khanhpham.wms.service;

import org.khanhpham.wms.domain.dto.UserDTO;

public interface UserService {
    UserDTO findByIdentity(String identity);
    UserDTO findById(Long id);
    UserDTO createUser(String email, String username);
    UserDTO findByUsernameOrEmail(String email, String username);
    void changePassword(String username, String password);
}
