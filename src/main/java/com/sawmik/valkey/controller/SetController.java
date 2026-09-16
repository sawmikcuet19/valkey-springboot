package com.sawmik.valkey.controller;

import com.sawmik.valkey.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/sets")
@RequiredArgsConstructor
public class SetController {

    private final StringValkeyTemplate stringRedisTemplate;

    @PostMapping("/{key}")
    public ResponseEntity<ApiResponse> add(@PathVariable String key, @RequestBody Map<String, Object> body) {
        List<String> values = (List<String>) body.get("values");
        Long count = stringRedisTemplate.opsForSet().add(key, values.toArray(new String[0]));
        return ResponseEntity.ok(ApiResponse.success("Members added successfully", count));
    }

    @GetMapping("/{key}")
    public ResponseEntity<ApiResponse> getAll(@PathVariable String key) {
        Set<String> members = stringRedisTemplate.opsForSet().members(key);
        return ResponseEntity.ok(ApiResponse.success("Set members retrieved", members));
    }

    @GetMapping("/{key}/size")
    public ResponseEntity<ApiResponse> size(@PathVariable String key) {
        Long size = stringRedisTemplate.opsForSet().size(key);
        return ResponseEntity.ok(ApiResponse.success("Set size retrieved", size));
    }

    @GetMapping("/{key}/member/{value}")
    public ResponseEntity<ApiResponse> isMember(@PathVariable String key, @PathVariable String value) {
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, value);
        return ResponseEntity.ok(ApiResponse.success("Membership check result", isMember));
    }

    @DeleteMapping("/{key}/member/{value}")
    public ResponseEntity<ApiResponse> removeMember(@PathVariable String key, @PathVariable String value) {
        Long removed = stringRedisTemplate.opsForSet().remove(key, value);
        return ResponseEntity.ok(ApiResponse.success("Member removed successfully", removed));
    }

    @PostMapping("/{key}/random")
    public ResponseEntity<ApiResponse> popRandom(@PathVariable String key) {
        String value = stringRedisTemplate.opsForSet().pop(key);
        if (value == null) {
            return ResponseEntity.ok(ApiResponse.error("Set is empty or key not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("Random member popped", value));
    }

    @PostMapping("/intersect")
    public ResponseEntity<ApiResponse> intersect(@RequestBody Map<String, List<String>> body) {
        List<String> keys = body.get("keys");
        Set<String> result = stringRedisTemplate.opsForSet().intersect(keys);
        return ResponseEntity.ok(ApiResponse.success("Intersection computed", result));
    }

    @PostMapping("/union")
    public ResponseEntity<ApiResponse> union(@RequestBody Map<String, List<String>> body) {
        List<String> keys = body.get("keys");
        Set<String> result = stringRedisTemplate.opsForSet().union(keys);
        return ResponseEntity.ok(ApiResponse.success("Union computed", result));
    }
}
