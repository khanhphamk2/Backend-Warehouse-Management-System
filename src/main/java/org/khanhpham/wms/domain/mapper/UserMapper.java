package org.khanhpham.wms.domain.mapper;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.khanhpham.wms.domain.dto.UserDTO;
import org.khanhpham.wms.domain.entity.User;
import org.khanhpham.wms.domain.request.RegisterRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {
    private ModelMapper modelMapper;

    public UserDTO convertToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User convertToEntity(@NotNull RegisterRequest registerRequest, String passwordEncoder) {
        return User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .name(registerRequest.getName())
                .password(passwordEncoder)
                .isActive(true)
                .build();
    }
}
