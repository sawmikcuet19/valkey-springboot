package com.sawmik.valkey.config;

import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.valkey.springframework.data.valkey.connection.ValkeyConnectionFactory;
import io.valkey.springframework.data.valkey.cache.ValkeyCacheManager;
import io.valkey.springframework.data.valkey.cache.ValkeyCacheConfiguration;
import io.valkey.springframework.data.valkey.serializer.GenericJackson2JsonValkeySerializer;
import io.valkey.springframework.data.valkey.serializer.ValkeySerializationContext;
import io.valkey.springframework.data.valkey.serializer.StringValkeySerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(ValkeyConnectionFactory connectionFactory) {
        ObjectMapper cacheObjectMapper = new ObjectMapper();
        cacheObjectMapper.registerModule(new JavaTimeModule());
        cacheObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        GenericJackson2JsonValkeySerializer jsonSerializer = new GenericJackson2JsonValkeySerializer(cacheObjectMapper);

        ValkeyCacheConfiguration config = ValkeyCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(ValkeySerializationContext.SerializationPair.fromSerializer(new StringValkeySerializer()))
                .serializeValuesWith(ValkeySerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        return ValkeyCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
