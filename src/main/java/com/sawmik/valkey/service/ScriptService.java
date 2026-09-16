package com.sawmik.valkey.service;

import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import io.valkey.springframework.data.valkey.core.script.DefaultValkeyScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScriptService {

    private final StringValkeyTemplate stringRedisTemplate;

    public Long atomicIncrementAndGet(String key) {
        DefaultValkeyScript<Long> script = new DefaultValkeyScript<>(
                "redis.call('incr', KEYS[1]) return redis.call('get', KEYS[1])",
                Long.class
        );
        return stringRedisTemplate.execute(script, Collections.singletonList(key));
    }

    public Boolean conditionalSet(String key, String expectedValue, String newValue) {
        DefaultValkeyScript<Boolean> script = new DefaultValkeyScript<>(
                "if redis.call('get', KEYS[1]) == ARGV[1] then redis.call('set', KEYS[1], ARGV[2]) return true else return false end",
                Boolean.class
        );
        return stringRedisTemplate.execute(script, Collections.singletonList(key), expectedValue, newValue);
    }

    public Long hashFieldIncrement(String key, String field, long increment) {
        DefaultValkeyScript<Long> script = new DefaultValkeyScript<>(
                "return redis.call('hincrby', KEYS[1], ARGV[1], ARGV[2])",
                Long.class
        );
        return stringRedisTemplate.execute(script, Collections.singletonList(key), field, String.valueOf(increment));
    }
}
