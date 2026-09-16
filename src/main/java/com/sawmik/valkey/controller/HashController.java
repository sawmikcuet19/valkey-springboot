package com.sawmik.valkey.controller;

import com.sawmik.valkey.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/hashes")
@RequiredArgsConstructor
public class HashController {

    private final StringValkeyTemplate stringRedisTemplate;

    @PostMapping("/{key}")
    public ResponseEntity<ApiResponse> put(@PathVariable String key, @RequestBody Map<String, String> body) {
        String field = body.get("field");
        String value = body.get("value");
        stringRedisTemplate.opsForHash().put(key, field, value);
        return ResponseEntity.ok(ApiResponse.success("Field-value pair set successfully", null));
    }

    @GetMapping("/{key}/{field}")
    public ResponseEntity<ApiResponse> get(@PathVariable String key, @PathVariable String field) {
        Object value = stringRedisTemplate.opsForHash().get(key, field);
        if (value == null) {
            return ResponseEntity.ok(ApiResponse.error("Field not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("Field value retrieved", value));
    }

    @GetMapping("/{key}/entries")
    public ResponseEntity<ApiResponse> entries(@PathVariable String key) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        return ResponseEntity.ok(ApiResponse.success("All entries retrieved", entries));
    }

    @GetMapping("/{key}/keys")
    public ResponseEntity<ApiResponse> keys(@PathVariable String key) {
        Set<Object> keys = stringRedisTemplate.opsForHash().keys(key);
        return ResponseEntity.ok(ApiResponse.success("All field names retrieved", keys));
    }

    @GetMapping("/{key}/size")
    public ResponseEntity<ApiResponse> size(@PathVariable String key) {
        Long size = stringRedisTemplate.opsForHash().size(key);
        return ResponseEntity.ok(ApiResponse.success("Hash size retrieved", size));
    }

    @DeleteMapping("/{key}/{field}")
    public ResponseEntity<ApiResponse> deleteField(@PathVariable String key, @PathVariable String field) {
        Long removed = stringRedisTemplate.opsForHash().delete(key, field);
        return ResponseEntity.ok(ApiResponse.success("Field deleted successfully", removed));
    }

    @PutMapping("/{key}/{field}/increment")
    public ResponseEntity<ApiResponse> incrementField(@PathVariable String key, @PathVariable String field,
                                                      @RequestBody Map<String, Long> body) {
        long delta = body.get("delta");
        Long newValue = stringRedisTemplate.opsForHash().increment(key, field, delta);
        return ResponseEntity.ok(ApiResponse.success("Field value incremented", newValue));
    }
}
