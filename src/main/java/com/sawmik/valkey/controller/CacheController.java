package com.sawmik.valkey.controller;

import com.sawmik.valkey.dto.ApiResponse;
import com.sawmik.valkey.service.CacheService;
import com.sawmik.valkey.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;
    private final DatabaseService databaseService;

    @GetMapping("/{key}")
    public ResponseEntity<ApiResponse> get(@PathVariable String key) {
        String value = cacheService.getFromDatabase(key);
        return ResponseEntity.ok(ApiResponse.success("Cache retrieved successfully", value));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<ApiResponse> evict(@PathVariable String key) {
        cacheService.evictCache(key);
        return ResponseEntity.ok(ApiResponse.success("Cache evicted successfully", key));
    }

    @DeleteMapping("/all")
    public ResponseEntity<ApiResponse> clearAll() {
        cacheService.clearCache();
        return ResponseEntity.ok(ApiResponse.success("Cache cleared successfully", null));
    }
}
