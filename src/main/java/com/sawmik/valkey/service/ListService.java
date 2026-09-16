package com.sawmik.valkey.service;

import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListService {

    private final StringValkeyTemplate stringRedisTemplate;

    public Long leftPush(String key, String value) {
        return stringRedisTemplate.opsForList().leftPush(key, value);
    }

    public Long rightPush(String key, String value) {
        return stringRedisTemplate.opsForList().rightPush(key, value);
    }

    public String leftPop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    public String rightPop(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    public String peek(String key) {
        return stringRedisTemplate.opsForList().index(key, 0);
    }

    public Long size(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    public List<String> range(String key, long start, long end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }

    public String index(String key, long index) {
        return stringRedisTemplate.opsForList().index(key, index);
    }

    public void set(String key, long index, String value) {
        stringRedisTemplate.opsForList().set(key, index, value);
    }

    public Long remove(String key, long count, String value) {
        return stringRedisTemplate.opsForList().remove(key, count, value);
    }

    public void trim(String key, long start, long end) {
        stringRedisTemplate.opsForList().trim(key, start, end);
    }
}
