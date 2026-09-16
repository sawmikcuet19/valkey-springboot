package com.sawmik.valkey.controller;

import com.sawmik.valkey.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/keys")
@RequiredArgsConstructor
public class KeyController {

    private final StringValkeyTemplate stringRedisTemplate;

    @DeleteMapping("/{key}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String key) {
        Boolean result = stringRedisTemplate.delete(key);
        return ResponseEntity.ok(ApiResponse.success("Key deleted successfully", result));
    }

    @GetMapping("/{key}/exists")
    public ResponseEntity<ApiResponse> exists(@PathVariable String key) {
        Boolean result = stringRedisTemplate.hasKey(key);
        return ResponseEntity.ok(ApiResponse.success("Key existence checked", result));
    }

    @PostMapping("/{key}/expire")
    public ResponseEntity<ApiResponse> expire(@PathVariable String key, @RequestBody Map<String, Long> body) {
        Long seconds = body.get("seconds");
        Boolean result = stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        return ResponseEntity.ok(ApiResponse.success("Expiry set successfully", result));
    }

    @GetMapping("/{key}/ttl")
    public ResponseEntity<ApiResponse> ttl(@PathVariable String key) {
        Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ResponseEntity.ok(ApiResponse.success("TTL retrieved", ttl));
    }

    @PostMapping("/{key}/persist")
    public ResponseEntity<ApiResponse> persist(@PathVariable String key) {
        Boolean result = stringRedisTemplate.persist(key);
        return ResponseEntity.ok(ApiResponse.success("TTL removed successfully", result));
    }

    @PostMapping("/rename")
    public ResponseEntity<ApiResponse> rename(@RequestBody Map<String, String> body) {
        String oldKey = body.get("oldKey");
        String newKey = body.get("newKey");
        Boolean exists = stringRedisTemplate.hasKey(oldKey);
        if (!Boolean.TRUE.equals(exists)) {
            return ResponseEntity.ok(ApiResponse.error("Key '" + oldKey + "' does not exist"));
        }
        stringRedisTemplate.rename(oldKey, newKey);
        return ResponseEntity.ok(ApiResponse.success("Key renamed successfully", true));
    }

    @GetMapping("/{key}/type")
    public ResponseEntity<ApiResponse> type(@PathVariable String key) {
        String type = stringRedisTemplate.type(key).name();
        return ResponseEntity.ok(ApiResponse.success("Key type retrieved", type));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> search(@RequestParam String pattern) {
        Set<String> keys = stringRedisTemplate.keys(pattern);
        return ResponseEntity.ok(ApiResponse.success("Keys found", keys));
    }
}
