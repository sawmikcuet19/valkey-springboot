package com.sawmik.valkey.controller;

import com.sawmik.valkey.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/sorted-sets")
@RequiredArgsConstructor
public class SortedSetController {

    private final StringValkeyTemplate stringRedisTemplate;

    @PostMapping("/{key}")
    public ResponseEntity<ApiResponse> add(@PathVariable String key, @RequestBody Map<String, Object> body) {
        String member = (String) body.get("member");
        double score = Double.parseDouble(body.get("score").toString());
        Boolean result = stringRedisTemplate.opsForZSet().add(key, member, score);
        return ResponseEntity.ok(ApiResponse.success("Member added to sorted set", result));
    }

    @GetMapping("/{key}")
    public ResponseEntity<ApiResponse> getAll(@PathVariable String key) {
        Set<String> range = stringRedisTemplate.opsForZSet().range(key, 0, -1);
        return ResponseEntity.ok(ApiResponse.success("Sorted set range retrieved", range));
    }

    @GetMapping("/{key}/rank/{member}")
    public ResponseEntity<ApiResponse> rank(@PathVariable String key, @PathVariable String member) {
        Long rank = stringRedisTemplate.opsForZSet().rank(key, member);
        return ResponseEntity.ok(ApiResponse.success("Rank retrieved", rank));
    }

    @GetMapping("/{key}/score/{member}")
    public ResponseEntity<ApiResponse> score(@PathVariable String key, @PathVariable String member) {
        Double score = stringRedisTemplate.opsForZSet().score(key, member);
        return ResponseEntity.ok(ApiResponse.success("Score retrieved", score));
    }

    @PostMapping("/{key}/increment/{member}")
    public ResponseEntity<ApiResponse> incrementScore(@PathVariable String key, @PathVariable String member,
                                                      @RequestBody Map<String, Double> body) {
        double delta = body.get("delta");
        Double newScore = stringRedisTemplate.opsForZSet().incrementScore(key, member, delta);
        return ResponseEntity.ok(ApiResponse.success("Score incremented", newScore));
    }

    @GetMapping("/{key}/range")
    public ResponseEntity<ApiResponse> rangeByScore(@PathVariable String key,
                                                    @RequestParam double min,
                                                    @RequestParam double max) {
        Set<String> result = stringRedisTemplate.opsForZSet().rangeByScore(key, min, max);
        return ResponseEntity.ok(ApiResponse.success("Range by score retrieved", result));
    }

    @DeleteMapping("/{key}/member/{member}")
    public ResponseEntity<ApiResponse> removeMember(@PathVariable String key, @PathVariable String member) {
        Long removed = stringRedisTemplate.opsForZSet().remove(key, member);
        return ResponseEntity.ok(ApiResponse.success("Member removed from sorted set", removed));
    }
}
