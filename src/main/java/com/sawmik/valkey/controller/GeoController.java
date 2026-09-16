package com.sawmik.valkey.controller;

import com.sawmik.valkey.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import io.valkey.springframework.data.valkey.connection.ValkeyGeoCommands;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/geo")
@RequiredArgsConstructor
public class GeoController {

    private final StringValkeyTemplate stringRedisTemplate;

    @PostMapping("/{key}")
    public ResponseEntity<ApiResponse> addLocation(@PathVariable String key,
                                                   @RequestBody Map<String, Object> body) {
        String member = (String) body.get("member");
        double longitude = Double.parseDouble(body.get("longitude").toString());
        double latitude = Double.parseDouble(body.get("latitude").toString());
        Point point = new Point(longitude, latitude);
        Long added = stringRedisTemplate.opsForGeo().add(key, point, member);
        return ResponseEntity.ok(ApiResponse.success("Location added successfully", added));
    }

    @GetMapping("/{key}/distance/{member1}/{member2}")
    public ResponseEntity<ApiResponse> distance(@PathVariable String key,
                                                @PathVariable String member1,
                                                @PathVariable String member2) {
        Distance distance = stringRedisTemplate.opsForGeo().distance(key, member1, member2, Metrics.KILOMETERS);
        return ResponseEntity.ok(ApiResponse.success("Distance retrieved", distance));
    }

    @GetMapping("/{key}/position/{member}")
    public ResponseEntity<ApiResponse> position(@PathVariable String key, @PathVariable String member) {
        List<Point> positions = stringRedisTemplate.opsForGeo().position(key, member);
        if (positions.isEmpty() || positions.get(0) == null) {
            return ResponseEntity.ok(ApiResponse.error("Member not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("Position retrieved", positions.get(0)));
    }

    @GetMapping("/{key}/radius")
    public ResponseEntity<ApiResponse> radius(@PathVariable String key,
                                              @RequestParam double longitude,
                                              @RequestParam double latitude,
                                              @RequestParam double radius,
                                              @RequestParam(defaultValue = "km") String unit) {
        Point point = new Point(longitude, latitude);
        io.valkey.springframework.data.valkey.domain.geo.Metrics metrics = switch (unit.toLowerCase()) {
            case "mi" -> io.valkey.springframework.data.valkey.domain.geo.Metrics.MILES;
            case "ft" -> io.valkey.springframework.data.valkey.domain.geo.Metrics.FEET;
            case "m" -> io.valkey.springframework.data.valkey.domain.geo.Metrics.METERS;
            default -> io.valkey.springframework.data.valkey.domain.geo.Metrics.KILOMETERS;
        };
        Distance distance = new Distance(radius, metrics);
        Circle circle = new Circle(point, distance);
        GeoResults<ValkeyGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo()
                .radius(key, circle, ValkeyGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates());
        return ResponseEntity.ok(ApiResponse.success("Radius search completed", results));
    }
}
