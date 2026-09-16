package com.sawmik.valkey.controller;

import com.sawmik.valkey.dto.ApiResponse;
import com.sawmik.valkey.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import io.valkey.springframework.data.valkey.core.StringValkeyTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;

@RestController
@RequestMapping("/api/server")
@RequiredArgsConstructor
public class ServerController {

    private final StringValkeyTemplate stringRedisTemplate;
    private final DatabaseService databaseService;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse> info() {
        Properties info = stringRedisTemplate.getConnectionFactory().getConnection().serverCommands().info();
        return ResponseEntity.ok(ApiResponse.success("Server info retrieved", info));
    }

    @GetMapping("/db-size")
    public ResponseEntity<ApiResponse> dbSize() {
        Long size = stringRedisTemplate.getConnectionFactory().getConnection().serverCommands().dbSize();
        return ResponseEntity.ok(ApiResponse.success("Database size retrieved", size));
    }

    @PostMapping("/flush")
    public ResponseEntity<ApiResponse> flush() {
        stringRedisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
        return ResponseEntity.ok(ApiResponse.success("All keys flushed successfully", null));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse> getAuditLogs() throws InterruptedException {
        var logs = databaseService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved (2s delay)", logs));
    }

    @GetMapping("/audit-logs/{id}")
    public ResponseEntity<ApiResponse> getAuditLogById(@PathVariable Long id) throws InterruptedException {
        var log = databaseService.findById(id);
        if (log.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("Audit log not found"));
        }
        return ResponseEntity.ok(ApiResponse.success("Audit log retrieved (2s delay)", log.get()));
    }
}
