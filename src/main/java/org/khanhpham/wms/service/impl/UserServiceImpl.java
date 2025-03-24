package org.khanhpham.wms.service.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.khanhpham.wms.domain.dto.UserDTO;
import org.khanhpham.wms.domain.entity.User;
import org.khanhpham.wms.domain.mapper.UserMapper;
import org.khanhpham.wms.domain.request.RegisterRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.UserRepository;
import org.khanhpham.wms.service.UserService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO findByIdentity(String identity) {
        try {
            Optional<User> user = userRepository.findByUsernameOrEmail(identity, identity);
            if (user.isEmpty()) {
                Long id = Long.parseLong(identity);
                user = userRepository.findById(id);
            }
            return user.map(userMapper::convertToDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "identity", identity));
        } catch (Exception e) {
            throw new ServiceException("Failed to find user by identity", e);
        }
    }

    @Override
    public UserDTO findById(Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
            return userMapper.convertToDTO(user);
        } catch (Exception e) {
            throw new ServiceException("Error retrieving user by ID: " + id, e);
        }
    }

    @Override
    @Transactional
    public UserDTO createUser(RegisterRequest registerRequest, String passwordEncoder) {
        try {
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                throw new ResourceAlreadyExistException("User", "email", registerRequest.getEmail());
            }

            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                throw new ResourceAlreadyExistException("User", "username", registerRequest.getUsername());
            }

            User user = userMapper.convertToEntity(registerRequest, passwordEncoder);
            return userMapper.convertToDTO(userRepository.save(user));
        } catch (Exception e) {
            throw new ServiceException("Error creating user", e);
        }
    }

    @Override
    public UserDTO findByUsernameOrEmail(String email, String username) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsernameOrEmail(username, email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email or username", email + " or " + username)));
        return user.map(userMapper::convertToDTO).orElse(null);
    }

    @Override
    @Transactional
    public void changePassword(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User", "username", username);
        }
        user.setPassword(password);
        userMapper.convertToDTO(userRepository.save(user));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return  userRepository.existsByEmail(email);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    public boolean existsByUsernameOrEmail(String identity) {
        return userRepository.existsByUsername(identity) || userRepository.existsByEmail(identity);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    public PaginationResponse<UserDTO> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<User> users = userRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<UserDTO> content = users.getContent()
                .stream()
                .map(userMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, users);
    }
}
