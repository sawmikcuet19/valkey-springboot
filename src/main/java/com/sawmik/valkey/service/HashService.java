package com.sawmik.valkey.service;

import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class HashService {

    private final StringValkeyTemplate stringRedisTemplate;

    public void put(String key, String field, String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    public Object get(String key, String field) {
        return stringRedisTemplate.opsForHash().get(key, field);
    }

    public Long delete(String key, Object... fields) {
        return stringRedisTemplate.opsForHash().delete(key, fields);
    }

    public Boolean hasKey(String key, String field) {
        return stringRedisTemplate.opsForHash().hasKey(key, field);
    }

    public Long increment(String key, String field, long delta) {
        return stringRedisTemplate.opsForHash().increment(key, field, delta);
    }

    public Map<Object, Object> entries(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public Set<Object> keys(String key) {
        return stringRedisTemplate.opsForHash().keys(key);
    }

    public java.util.List<Object> values(String key) {
        return stringRedisTemplate.opsForHash().values(key);
    }

    public Long size(String key) {
        return stringRedisTemplate.opsForHash().size(key);
    }

    public void multiPut(String key, Map<String, String> map) {
        stringRedisTemplate.opsForHash().putAll(key, map);
    }

    public Boolean setIfAbsent(String key, String field, String value) {
        return stringRedisTemplate.opsForHash().putIfAbsent(key, field, value);
    }
}
