package com.sawmik.valkey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ValkeySpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ValkeySpringbootApplication.class, args);
    }
}
