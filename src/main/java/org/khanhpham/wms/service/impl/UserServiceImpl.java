package org.khanhpham.wms.service.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.khanhpham.wms.domain.dto.UserDTO;
import org.khanhpham.wms.domain.model.User;
import org.khanhpham.wms.domain.request.RegisterRequest;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.UserRepository;
import org.khanhpham.wms.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private UserDTO convertToDTO(Object object) {
        return modelMapper.map(object, UserDTO.class);
    }

    public User convertToEntity(RegisterRequest registerRequest, String passwordEncoder) {
        return User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .name(registerRequest.getName())
                .password(passwordEncoder)
                .build();
    }

    @Override
    public UserDTO findByIdentity(String identity) {
        try {
            Optional<User> user = userRepository.findByUsernameOrEmail(identity, identity);
            if (user.isEmpty()) {
                Long id = Long.parseLong(identity);
                user = userRepository.findById(id);
            }
            return user.map(this::convertToDTO)
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
            return convertToDTO(user);
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

            User user = convertToEntity(registerRequest, passwordEncoder);
            return convertToDTO(userRepository.save(user));
        } catch (Exception e) {
            throw new ServiceException("Error creating user", e);
        }
    }

    @Override
    public UserDTO findByUsernameOrEmail(String email, String username) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsernameOrEmail(username, email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email or username", email + " or " + username)));
        return user.map(this::convertToDTO).orElse(null);
    }

    @Override
    @Transactional
    public void changePassword(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User", "username", username);
        }
        user.setPassword(password);
        convertToDTO(userRepository.save(user));
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
}
