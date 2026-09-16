package com.sawmik.valkey.service;

import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.connection.stream.*;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StreamService {

    private final StringValkeyTemplate stringRedisTemplate;

    public RecordId addMessage(String stream, Map<String, String> message) {
        return stringRedisTemplate.opsForStream().add(stream, message);
    }

    public List<MapRecord<String, Object, Object>> readMessages(String stream, long count) {
        return stringRedisTemplate.opsForStream().read(
                StreamReadOptions.empty().count(count),
                StreamOffset.create(stream, ReadOffset.from("0"))
        );
    }

    public void createGroup(String stream, String groupName) {
        stringRedisTemplate.opsForStream().createGroup(stream, groupName);
    }

    public List<MapRecord<String, Object, Object>> readGroup(String stream, String groupName, String consumerName, long count) {
        return stringRedisTemplate.opsForStream().read(
                Consumer.from(groupName, consumerName),
                StreamReadOptions.empty().count(count),
                StreamOffset.create(stream, ReadOffset.lastConsumed())
        );
    }

    public Long acknowledge(String stream, String groupName, String... messageIds) {
        return stringRedisTemplate.opsForStream().acknowledge(stream, groupName, messageIds);
    }

    public PendingMessagesSummary pending(String stream, String groupName) {
        return stringRedisTemplate.opsForStream().pending(stream, groupName);
    }

    public Long trim(String stream, long count) {
        return stringRedisTemplate.opsForStream().trim(stream, count);
    }

    public StreamInfo.XInfoStream info(String stream) {
        return stringRedisTemplate.opsForStream().info(stream);
    }
}
