package org.khanhpham.wms.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.khanhpham.wms.service.CacheService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void evictByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.isEmpty()) {
            log.warn("No cache found with pattern: {}", pattern);
            return;
        }
        redisTemplate.delete(keys);
    }

    @Override
    public void evictByKeys(String... keys) {
        if (keys != null && keys.length > 0) {
            redisTemplate.delete(Arrays.asList(keys));
        }
    }

    @Override
    public void cacheValue(String key, Object value, Duration duration) {
        Optional.ofNullable(value)
                .ifPresent(val -> redisTemplate.opsForValue().set(key, val, duration));
    }

    @Override
    public <T> T getCached(
            String key,
            TypeReference<T> typeReference,
            Supplier<T> dbSupplier,
            Duration duration
    ) {
        return deserializeOrFetchAndCache(key, typeReference, dbSupplier, duration);
    }

    @Override
    public <T> T getCachedValue(String key, TypeReference<T> typeReference) {
        Object value = getByKey(key);
        return deserializeValue(key, typeReference, value);
    }

    private Object getByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    private <T> @Nullable T deserializeValue(String key, TypeReference<T> typeReference, Object value) {
        if (value == null) {
            log.debug("No cached value found for key: {}", key);
            return null;
        }
        try {
            return objectMapper.convertValue(value, typeReference);
        } catch (IllegalArgumentException e) {
            handleDeserializationError(key, e);
            return null;
        }
    }

    private <T> T deserializeOrFetchAndCache(
            String key,
            TypeReference<T> typeReference,
            Supplier<T> supplier,
            Duration duration
    ) {
        Object cachedValue = getByKey(key);
        T result = deserializeValue(key, typeReference, cachedValue);

        if (result == null) {
            result = supplier.get();
            if (result != null) {
                cacheValue(key, result, duration);
            }
        }

        return result;
    }

    private void handleDeserializationError(String key, Exception e) {
        log.error("Failed to deserialize cache for key: {}, removing from cache", key, e);
        redisTemplate.delete(key);
    }
}
