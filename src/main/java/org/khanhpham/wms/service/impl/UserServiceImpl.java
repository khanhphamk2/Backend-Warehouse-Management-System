package org.khanhpham.wms.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.khanhpham.wms.domain.dto.UserDTO;
import org.khanhpham.wms.domain.entity.User;
import org.khanhpham.wms.domain.mapper.UserMapper;
import org.khanhpham.wms.domain.request.RegisterRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.khanhpham.wms.exception.ResourceAlreadyExistException;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.UserRepository;
import org.khanhpham.wms.service.CacheService;
import org.khanhpham.wms.service.UserService;
import org.khanhpham.wms.utils.PaginationUtils;
import org.khanhpham.wms.utils.RedisKeyUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER = "User";
    private static final String USERNAME = "username";
    private static final String IDENTITY = "identity";
    private static final String REDIS_PREFIX_PATTERN = "users:page:";
    private static final Duration REDIS_TTL = Duration.ofMinutes(15);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CacheService cacheService;

    @Override
    public UserDTO findByIdentity(String identity) {
        Long userId = getOrCache(
                RedisKeyUtils.generateKey(USER, IDENTITY, identity),
                new TypeReference<>() {},
                () -> findByUsernameOrEmail(identity).getId()
        );

        return findById(userId);
    }

    @Override
    public UserDTO findById(Long id) {
        return getOrCache(
                RedisKeyUtils.generateIdKey(USER, id),
                new TypeReference<>() {},
                () -> userMapper.convertToDTO(getUser(id))
        );
    }

    @Override
    @Transactional
    public UserDTO createUser(@NotNull RegisterRequest registerRequest, String passwordEncoder) {
        validateUserExistence(registerRequest.getUsername(), registerRequest.getEmail());
        User user = userMapper.convertToEntity(registerRequest, passwordEncoder);
        UserDTO savedUser = save(user);

        cacheUser(savedUser);

        return savedUser;
    }

    @Override
    @Transactional
    public void changePassword(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User", USERNAME, username);
        }
        user.setPassword(password);
        UserDTO updatedUser = save(user);
        evictUserCache(updatedUser);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public Optional<User> getByEmail(String email) {
        return Optional.ofNullable(getOrCache(
                RedisKeyUtils.generateKey(USER, IDENTITY, email),
                new TypeReference<>() {},
                () -> findByEmail(email)
                )
        );
    }

    @Override
    public boolean existsByUsernameOrEmail(String identity) {
        return userRepository.existsByUsername(identity) || userRepository.existsByEmail(identity);
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    public PaginationResponse<UserDTO> getAllUsers(
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        return cacheService.getCached(
                RedisKeyUtils.generatePatternKey("users", pageNumber, pageSize, sortBy, sortDir),
                new TypeReference<>() {},
                () -> getAllUsersFromDB(pageNumber, pageSize, sortBy, sortDir),
                REDIS_TTL
        );
    }

    // ----------- Private Helpers -----------

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private User findByUsernameOrEmail(String identity) {
        return userRepository.findByUsernameOrEmail(identity, identity)
                .or(() -> parseId(identity).flatMap(userRepository::findById))
                .orElseThrow(() -> new ResourceNotFoundException("User", IDENTITY, identity));
    }

    private UserDTO save(User user) {
        return userMapper.convertToDTO(userRepository.save(user));
    }

    private Optional<Long> parseId(String identity) {
        try {
            return Optional.of(Long.parseLong(identity));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private @NotNull PaginationResponse<UserDTO> getAllUsersFromDB(
            int pageNumber, int pageSize, String sortBy, String sortDir) {
        Page<User> users = userRepository.findAll(
                PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir)
        );

        List<UserDTO> content = users.getContent()
                .stream()
                .map(userMapper::convertToDTO)
                .toList();

        return PaginationUtils.createPaginationResponse(content, users);
    }

    private void validateUserExistence(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new ResourceAlreadyExistException(USER, USERNAME, username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistException(USER, "email", email);
        }

    }

    private <T> T getOrCache(String key, TypeReference<T> typeRef, Supplier<T> dbSupplier) {
        return cacheService.getCached(key, typeRef, dbSupplier, REDIS_TTL);
    }

    private void cacheUser(UserDTO userDTO) {
        cacheService.cacheValue(
                RedisKeyUtils.generateIdKey(USER, userDTO.getId()),
                userDTO,
                REDIS_TTL
        );

        buildAliasKeys(userDTO).forEach(
                key -> cacheService.cacheValue(key, userDTO.getId(), REDIS_TTL)
        );

        cacheService.evictByPattern(REDIS_PREFIX_PATTERN + "*");
    }

    private void evictUserCache(@NotNull UserDTO userDTO) {
        List<String> keys = new ArrayList<>();
        keys.add(RedisKeyUtils.generateIdKey(USER, userDTO.getId()));
        keys.addAll(buildAliasKeys(userDTO));
        cacheService.evictByKeys(keys.toArray(String[]::new));
    }

    @Contract("_ -> new")
    private @NotNull @Unmodifiable List<String> buildAliasKeys(@NotNull UserDTO userDTO) {
        return List.of(
                RedisKeyUtils.generateKey(USER, USERNAME, userDTO.getUsername()),
                RedisKeyUtils.generateKey(USER, IDENTITY, userDTO.getEmail()),
                RedisKeyUtils.generateKey(USER, IDENTITY, userDTO.getUsername())
        );
    }
}
