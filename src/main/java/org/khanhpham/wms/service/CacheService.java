package org.khanhpham.wms.service;

import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.function.Supplier;

public interface CacheService {
    void evictByPattern(String pattern);
    void evictByKeys(String... keys);
    void cacheValue(String key, Object value, Duration duration);
    <T> T getCached(String key, TypeReference<T> typeReference, Supplier<T> dbSupplier, Duration duration);
    <T> T getCachedValue(String key, TypeReference<T> typeReference);
}
