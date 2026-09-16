package com.sawmik.valkey.service;

import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class KeyService {

    private final StringValkeyTemplate stringRedisTemplate;

    public Long delete(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }

    public Boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public Boolean expire(String key, long timeout, java.util.concurrent.TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }

    public Long ttl(String key) {
        return stringRedisTemplate.getExpire(key);
    }

    public Boolean persist(String key) {
        return stringRedisTemplate.persist(key);
    }

    public void rename(String oldKey, String newKey) {
        stringRedisTemplate.rename(oldKey, newKey);
    }

    public String type(String key) {
        return stringRedisTemplate.type(key).name();
    }

    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    public String randomKey() {
        return stringRedisTemplate.randomKey();
    }

    public byte[] dump(String key) {
        return stringRedisTemplate.dump(key);
    }

    public void restore(String key, byte[] value, long timeout, java.util.concurrent.TimeUnit unit) {
        stringRedisTemplate.restore(key, value, timeout, unit);
    }
}
