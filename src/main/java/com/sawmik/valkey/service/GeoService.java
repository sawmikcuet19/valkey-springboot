package com.sawmik.valkey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import io.valkey.springframework.data.valkey.connection.ValkeyGeoCommands;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeoService {

    private final StringValkeyTemplate stringRedisTemplate;

    public Long addLocation(String key, Point point, String member) {
        return stringRedisTemplate.opsForGeo().add(key, point, member);
    }

    public Distance distance(String key, String member1, String member2) {
        return stringRedisTemplate.opsForGeo().distance(key, member1, member2);
    }

    public List<Point> position(String key, String... members) {
        return stringRedisTemplate.opsForGeo().position(key, members);
    }

    public GeoResults<ValkeyGeoCommands.GeoLocation<String>> radiusSearch(String key, Point point, double radius) {
        Circle circle = new Circle(point, new Distance(radius, ValkeyGeoCommands.DistanceUnit.MILES));
        return stringRedisTemplate.opsForGeo().radius(key, circle);
    }

    public GeoResults<ValkeyGeoCommands.GeoLocation<String>> radiusSearchByMember(String key, String member, double radius) {
        Circle circle = new Circle(
                (Point) stringRedisTemplate.opsForGeo().position(key, member).get(0),
                new Distance(radius, ValkeyGeoCommands.DistanceUnit.MILES)
        );
        return stringRedisTemplate.opsForGeo().radius(key, circle);
    }

    public List<String> hash(String key, String... members) {
        return stringRedisTemplate.opsForGeo().hash(key, members);
    }
}
