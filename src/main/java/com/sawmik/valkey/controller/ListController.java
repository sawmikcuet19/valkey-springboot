package com.sawmik.valkey.controller;

import com.sawmik.valkey.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ListController {

    private final StringValkeyTemplate stringRedisTemplate;

    @PostMapping("/{key}/left")
    public ResponseEntity<ApiResponse> leftPush(@PathVariable String key, @RequestBody Map<String, String> body) {
        String value = body.get("value");
        Long size = stringRedisTemplate.opsForList().leftPush(key, value);
        return ResponseEntity.ok(ApiResponse.success("Left push successful", size));
    }

    @PostMapping("/{key}/right")
    public ResponseEntity<ApiResponse> rightPush(@PathVariable String key, @RequestBody Map<String, String> body) {
        String value = body.get("value");
        Long size = stringRedisTemplate.opsForList().rightPush(key, value);
        return ResponseEntity.ok(ApiResponse.success("Right push successful", size));
    }

    @GetMapping("/{key}/left")
    public ResponseEntity<ApiResponse> leftPop(@PathVariable String key) {
        String value = stringRedisTemplate.opsForList().leftPop(key);
        if (value == null) {
            return ResponseEntity.ok(ApiResponse.error("List is empty or key not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("Left pop successful", value));
    }

    @GetMapping("/{key}/right")
    public ResponseEntity<ApiResponse> rightPop(@PathVariable String key) {
        String value = stringRedisTemplate.opsForList().rightPop(key);
        if (value == null) {
            return ResponseEntity.ok(ApiResponse.error("List is empty or key not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("Right pop successful", value));
    }

    @GetMapping("/{key}")
    public ResponseEntity<ApiResponse> getAll(@PathVariable String key) {
        List<String> elements = stringRedisTemplate.opsForList().range(key, 0, -1);
        return ResponseEntity.ok(ApiResponse.success("List retrieved successfully", elements));
    }

    @GetMapping("/{key}/size")
    public ResponseEntity<ApiResponse> size(@PathVariable String key) {
        Long size = stringRedisTemplate.opsForList().size(key);
        return ResponseEntity.ok(ApiResponse.success("List size retrieved", size));
    }

    @GetMapping("/{key}/{index}")
    public ResponseEntity<ApiResponse> getByIndex(@PathVariable String key, @PathVariable Long index) {
        String value = stringRedisTemplate.opsForList().index(key, index);
        if (value == null) {
            return ResponseEntity.ok(ApiResponse.error("Element not found at index"));
        }
        return ResponseEntity.ok(ApiResponse.success("Element retrieved successfully", value));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String key) {
        stringRedisTemplate.delete(key);
        return ResponseEntity.ok(ApiResponse.success("List deleted successfully", null));
    }
}
