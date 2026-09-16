package com.sawmik.valkey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final StringValkeyTemplate stringRedisTemplate;

    @Cacheable(value = "cache_demo", key = "#key")
    public String getFromDatabase(String key) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Database value for " + key;
    }

    @CacheEvict(value = "cache_demo", key = "#key")
    public void evictCache(String key) {
    }

    @CacheEvict(value = "cache_demo", allEntries = true)
    public void clearCache() {
    }
}
