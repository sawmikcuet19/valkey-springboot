package com.sawmik.valkey.controller;

import com.sawmik.valkey.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hyperloglog")
@RequiredArgsConstructor
public class HyperLogLogController {

    private final StringValkeyTemplate stringRedisTemplate;

    @PostMapping("/{key}/add")
    public ResponseEntity<ApiResponse> add(@PathVariable String key, @RequestBody Map<String, List<String>> body) {
        List<String> values = body.get("values");
        Long added = stringRedisTemplate.opsForHyperLogLog().add(key, values.toArray(new String[0]));
        return ResponseEntity.ok(ApiResponse.success("Elements added to HyperLogLog", added));
    }

    @GetMapping("/{key}/count")
    public ResponseEntity<ApiResponse> count(@PathVariable String key) {
        Long size = stringRedisTemplate.opsForHyperLogLog().size(key);
        return ResponseEntity.ok(ApiResponse.success("Approximate count retrieved", size));
    }

    @PostMapping("/merge")
    public ResponseEntity<ApiResponse> merge(@RequestBody Map<String, Object> body) {
        String destKey = (String) body.get("destKey");
        List<String> sourceKeys = (List<String>) body.get("sourceKeys");
        stringRedisTemplate.opsForHyperLogLog().union(destKey, sourceKeys.toArray(new String[0]));
        return ResponseEntity.ok(ApiResponse.success("HyperLogLogs merged successfully", null));
    }
}
