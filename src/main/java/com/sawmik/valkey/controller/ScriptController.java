package com.sawmik.valkey.controller;

import com.sawmik.valkey.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import io.valkey.springframework.data.valkey.core.script.DefaultValkeyScript;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scripts")
@RequiredArgsConstructor
public class ScriptController {

    private final StringValkeyTemplate stringRedisTemplate;

    @PostMapping("/atomic-increment")
    public ResponseEntity<ApiResponse> atomicIncrement(@RequestBody Map<String, String> body) {
        String key = body.get("key");
        DefaultValkeyScript<Long> script = new DefaultValkeyScript<>(
                "local current = redis.call('GET', KEYS[1]) " +
                "if current then " +
                "  local newVal = tonumber(current) + 1 " +
                "  redis.call('SET', KEYS[1], tostring(newVal)) " +
                "  return newVal " +
                "else " +
                "  redis.call('SET', KEYS[1], '1') " +
                "  return 1 " +
                "end", Long.class);
        Long result = stringRedisTemplate.execute(script, List.of(key));
        return ResponseEntity.ok(ApiResponse.success("Atomic increment completed", result));
    }

    @PostMapping("/conditional-set")
    public ResponseEntity<ApiResponse> conditionalSet(@RequestBody Map<String, String> body) {
        String key = body.get("key");
        String expectedValue = body.get("expectedValue");
        String newValue = body.get("newValue");
        DefaultValkeyScript<Long> script = new DefaultValkeyScript<>(
                "local current = redis.call('GET', KEYS[1]) " +
                "if current == ARGV[1] then " +
                "  redis.call('SET', KEYS[1], ARGV[2]) " +
                "  return 1 " +
                "else " +
                "  return 0 " +
                "end", Long.class);
        Long result = stringRedisTemplate.execute(script, List.of(key), expectedValue, newValue);
        return ResponseEntity.ok(ApiResponse.success("Conditional set completed", result));
    }
}
