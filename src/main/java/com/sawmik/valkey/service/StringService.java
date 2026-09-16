package com.sawmik.valkey.service;

import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StringService {

    private final StringValkeyTemplate stringRedisTemplate;

    public String set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
        return value;
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public String setWithExpiry(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
        return value;
    }

    public Integer append(String key, String value) {
        return stringRedisTemplate.opsForValue().append(key, value);
    }

    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    public Long decrement(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    public Boolean setIfAbsent(String key, String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public Boolean setIfExist(String key, String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public String getAndSet(String key, String value) {
        return stringRedisTemplate.opsForValue().getAndSet(key, value);
    }

    public List<String> multiGet(List<String> keys) {
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    public Long stringLength(String key) {
        return stringRedisTemplate.opsForValue().size(key);
    }

    public Long bitCount(String key) {
        return stringRedisTemplate.execute((io.valkey.springframework.data.valkey.core.ValkeyCallback<Long>) connection ->
                connection.stringCommands().bitCount(key.getBytes()));
    }

    public Boolean setRange(String key, String value, long offset) {
        stringRedisTemplate.opsForValue().set(key, value, offset);
        return true;
    }
}
