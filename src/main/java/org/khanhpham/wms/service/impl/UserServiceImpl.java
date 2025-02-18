package org.khanhpham.wms.service.impl;

import org.khanhpham.wms.domain.dto.UserDTO;
import org.khanhpham.wms.domain.model.User;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.UserRepository;
import org.khanhpham.wms.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    private UserDTO convertToDTO(Object object) {
        return modelMapper.map(object, UserDTO.class);
    }

    public User convertToEntity(String email, String username) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        return user;
    }

    @Override
    public UserDTO findByIdentity(String identity) {
        Optional<User> user = userRepository.findByUsernameOrEmail(identity, identity);
        if (user.isEmpty()) {
            try {
                Long id = Long.parseLong(identity);
                user = userRepository.findById(id);
            } catch (NumberFormatException e) {
                throw new ResourceNotFoundException("User", "identity", identity);
            }
        }
        return user.map(this::convertToDTO).orElseThrow(() -> new ResourceNotFoundException("User", "identity", identity));
    }

    @Override
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return convertToDTO(user);
    }

    @Override
    public UserDTO createUser(String email, String username) {
        User user = convertToEntity(email, username);
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public UserDTO findByUsernameOrEmail(String email, String username) {
        Optional<User> user = userRepository.findByUsernameOrEmail(username, email);
        return user.map(this::convertToDTO).orElse(null);
    }

    @Override
    public void changePassword(String username, String password) {
        User user = userRepository.findByUsername(username);
        user.setPassword(password);
        convertToDTO(userRepository.save(user));
    }
}
