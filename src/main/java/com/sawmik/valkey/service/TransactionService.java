package com.sawmik.valkey.service;

import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final StringValkeyTemplate stringRedisTemplate;

    public List<Object> executeTransaction(Map<String, String> keyValuePairs) {
        return stringRedisTemplate.execute((io.valkey.springframework.data.valkey.core.ValkeyCallback<List<Object>>) connection -> {
            connection.multi();
            for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
                connection.set(entry.getKey().getBytes(), entry.getValue().getBytes());
            }
            return connection.exec();
        });
    }

    public List<Object> executePipeline(List<Map.Entry<String, String>> operations) {
        return stringRedisTemplate.executePipelined((io.valkey.springframework.data.valkey.core.ValkeyCallback<Object>) connection -> {
            for (Map.Entry<String, String> entry : operations) {
                connection.set(entry.getKey().getBytes(), entry.getValue().getBytes());
            }
            return null;
        });
    }
}
