package org.khanhpham.wms.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class RedisKeyUtils {
    @Contract(pure = true)
    public static @NotNull String generateKey(@NotNull String prefix, @NotNull String key, String value) {
        return prefix.toLowerCase() + ":" + key.toLowerCase() + ":" + value;
    }

    @Contract(pure = true)
    public static @NotNull String generatePatternKey(@NotNull String prefix,
                                                     int pageNumber, int pageSize,
                                                     @NotNull String sortBy, @NotNull String sortDir) {
        return prefix.toLowerCase() + ":page:" + pageNumber + ":" +
                pageSize + ":" + sortBy.toLowerCase() + ":" + sortDir.toLowerCase();
    }

    @Contract(pure = true)
    public static @NotNull String generateIdKey(@NotNull String prefix, Long id) {
        return prefix.toLowerCase() + ":id:" + id;
    }
}
