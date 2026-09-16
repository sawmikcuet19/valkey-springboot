package com.sawmik.valkey.controller;

import com.sawmik.valkey.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.connection.stream.*;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.data.domain.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/streams")
@RequiredArgsConstructor
public class StreamController {

    private final StringValkeyTemplate stringRedisTemplate;

    @PostMapping("/{streamName}")
    public ResponseEntity<ApiResponse> addMessage(@PathVariable String streamName,
                                                  @RequestBody Map<String, String> body) {
        RecordId recordId = stringRedisTemplate.opsForStream().add(streamName, body);
        return ResponseEntity.ok(ApiResponse.success("Message added to stream", recordId));
    }

    @GetMapping("/{streamName}")
    public ResponseEntity<ApiResponse> readAll(@PathVariable String streamName) {
        List<MapRecord<String, Object, Object>> records = stringRedisTemplate.opsForStream()
                .range(streamName, Range.unbounded());
        return ResponseEntity.ok(ApiResponse.success("Stream messages retrieved", records));
    }

    @PostMapping("/{streamName}/group/{groupName}")
    public ResponseEntity<ApiResponse> createConsumerGroup(@PathVariable String streamName,
                                                           @PathVariable String groupName) {
        try {
            stringRedisTemplate.opsForStream().createGroup(streamName, groupName);
            return ResponseEntity.ok(ApiResponse.success("Consumer group created successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{streamName}/group/{groupName}/read")
    public ResponseEntity<ApiResponse> readFromGroup(@PathVariable String streamName,
                                                     @PathVariable String groupName,
                                                     @RequestBody Map<String, String> body) {
        String consumer = body.get("consumer");
        Consumer consumerObj = Consumer.from(groupName, consumer);
        StreamReadOptions readOptions = StreamReadOptions.empty().count(10);
        List<MapRecord<String, Object, Object>> records = stringRedisTemplate.opsForStream()
                .read(consumerObj, readOptions, StreamOffset.create(streamName, ReadOffset.lastConsumed()));
        return ResponseEntity.ok(ApiResponse.success("Messages read from group", records));
    }

    @PostMapping("/{streamName}/group/{groupName}/ack")
    public ResponseEntity<ApiResponse> acknowledge(@PathVariable String streamName,
                                                   @PathVariable String groupName,
                                                   @RequestBody Map<String, Object> body) {
        List<String> recordIds = (List<String>) body.get("recordIds");
        Long acknowledged = stringRedisTemplate.opsForStream()
                .acknowledge(streamName, groupName, recordIds.stream().toArray(String[]::new));
        return ResponseEntity.ok(ApiResponse.success("Messages acknowledged", acknowledged));
    }
}
