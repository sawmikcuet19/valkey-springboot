package com.sawmik.valkey.service;

import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import io.valkey.springframework.data.valkey.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SortedSetService {

    private final StringValkeyTemplate stringRedisTemplate;

    public Boolean add(String key, String value, double score) {
        return stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    public Set<String> range(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    public Set<String> rangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    public Long rank(String key, String value) {
        return stringRedisTemplate.opsForZSet().rank(key, value);
    }

    public Double score(String key, String value) {
        return stringRedisTemplate.opsForZSet().score(key, value);
    }

    public Double incrementScore(String key, String value, double delta) {
        return stringRedisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    public Long remove(String key, String... values) {
        return stringRedisTemplate.opsForZSet().remove(key, values);
    }

    public Long count(String key) {
        return stringRedisTemplate.opsForZSet().zCard(key);
    }

    public Set<ZSetOperations.TypedTuple<String>> rangeWithScores(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }
}
