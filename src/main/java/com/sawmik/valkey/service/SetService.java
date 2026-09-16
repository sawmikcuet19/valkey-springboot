package com.sawmik.valkey.service;

import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SetService {

    private final StringValkeyTemplate stringRedisTemplate;

    public Long add(String key, String... values) {
        return stringRedisTemplate.opsForSet().add(key, values);
    }

    public Long remove(String key, String... values) {
        return stringRedisTemplate.opsForSet().remove(key, values);
    }

    public Set<String> members(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    public Boolean isMember(String key, String value) {
        return stringRedisTemplate.opsForSet().isMember(key, value);
    }

    public Long size(String key) {
        return stringRedisTemplate.opsForSet().size(key);
    }

    public String randomMember(String key) {
        return stringRedisTemplate.opsForSet().randomMember(key);
    }

    public String pop(String key) {
        return stringRedisTemplate.opsForSet().pop(key);
    }

    public Set<String> intersect(String key1, String key2) {
        return stringRedisTemplate.opsForSet().intersect(key1, key2);
    }

    public Set<String> union(String key1, String key2) {
        return stringRedisTemplate.opsForSet().union(key1, key2);
    }

    public Set<String> difference(String key1, String key2) {
        return stringRedisTemplate.opsForSet().difference(key1, key2);
    }

    public Boolean move(String key, String value, String destKey) {
        return stringRedisTemplate.opsForSet().move(key, value, destKey);
    }
}
