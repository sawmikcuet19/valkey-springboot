package com.sawmik.valkey.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import io.valkey.springframework.data.valkey.core.ValkeyTemplate;
import io.valkey.springframework.data.valkey.serializer.Jackson2JsonValkeySerializer;
import io.valkey.springframework.data.valkey.serializer.StringValkeySerializer;

@Configuration
public class ValkeyConfig {

    @Bean
    public ValkeyTemplate<String, Object> redisTemplate(StringValkeyTemplate stringRedisTemplate) {
        ValkeyTemplate<String, Object> template = new ValkeyTemplate<>();
        template.setConnectionFactory(stringRedisTemplate.getConnectionFactory());
        template.setKeySerializer(new StringValkeySerializer());
        template.setHashKeySerializer(new StringValkeySerializer());
        template.setValueSerializer(new Jackson2JsonValkeySerializer<>(Object.class));
        template.setHashValueSerializer(new Jackson2JsonValkeySerializer<>(Object.class));
        template.afterPropertiesSet();
        return template;
    }
}
