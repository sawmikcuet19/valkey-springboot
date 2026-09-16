package com.sawmik.valkey.service;

import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HyperLogLogService {

    private final StringValkeyTemplate stringRedisTemplate;

    public Long add(String key, String... values) {
        return stringRedisTemplate.opsForHyperLogLog().add(key, values);
    }

    public Long count(String... keys) {
        return stringRedisTemplate.opsForHyperLogLog().size(keys);
    }

    public void merge(String destination, String... sourceKeys) {
        stringRedisTemplate.opsForHyperLogLog().union(destination, sourceKeys);
    }
}
