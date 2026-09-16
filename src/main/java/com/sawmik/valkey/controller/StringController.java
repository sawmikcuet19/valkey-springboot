package com.sawmik.valkey.controller;

import com.sawmik.valkey.dto.ApiResponse;
import com.sawmik.valkey.service.StringService;
import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/strings")
@RequiredArgsConstructor
public class StringController {

    private final StringService stringService;
    private final StringValkeyTemplate stringRedisTemplate;

    @PostMapping
    public ResponseEntity<ApiResponse> set(@RequestBody Map<String, Object> body) {
        String key = (String) body.get("key");
        String value = (String) body.get("value");
        Object ttlSeconds = body.get("ttlSeconds");
        if (ttlSeconds != null) {
            stringService.setWithExpiry(key, value, Long.parseLong(ttlSeconds.toString()), TimeUnit.SECONDS);
        } else {
            stringService.set(key, value);
        }
        return ResponseEntity.ok(ApiResponse.success("Key set successfully", value));
    }

    @GetMapping("/{key}")
    public ResponseEntity<ApiResponse> get(@PathVariable String key) {
        String value = stringService.get(key);
        if (value == null) {
            return ResponseEntity.ok(ApiResponse.error("Key not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("Key retrieved successfully", value));
    }

    @PutMapping("/{key}/append")
    public ResponseEntity<ApiResponse> append(@PathVariable String key, @RequestBody Map<String, String> body) {
        String value = body.get("value");
        Integer newLength = stringService.append(key, value);
        return ResponseEntity.ok(ApiResponse.success("Value appended successfully", newLength));
    }

    @PostMapping("/{key}/increment")
    public ResponseEntity<ApiResponse> increment(@PathVariable String key) {
        Long newValue = stringService.increment(key);
        return ResponseEntity.ok(ApiResponse.success("Value incremented successfully", newValue));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String key) {
        stringRedisTemplate.delete(key);
        return ResponseEntity.ok(ApiResponse.success("Key deleted successfully", null));
    }

    @GetMapping("/{key}/length")
    public ResponseEntity<ApiResponse> length(@PathVariable String key) {
        Long length = stringService.stringLength(key);
        return ResponseEntity.ok(ApiResponse.success("String length retrieved", length));
    }

    @PostMapping("/check-absent")
    public ResponseEntity<ApiResponse> setIfAbsent(@RequestBody Map<String, String> body) {
        String key = body.get("key");
        String value = body.get("value");
        Boolean result = stringService.setIfAbsent(key, value);
        return ResponseEntity.ok(ApiResponse.success("Set if absent result", result));
    }
}
