package com.sawmik.valkey.service;

import com.sawmik.valkey.entity.AuditLog;
import com.sawmik.valkey.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final AuditLogRepository auditLogRepository;

    public AuditLog simulateSlowQuery(String operation, String entityType, String entityId, String details) throws InterruptedException {
        Thread.sleep(2000);
        AuditLog auditLog = AuditLog.builder()
                .operation(operation)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();
        return auditLogRepository.save(auditLog);
    }

    public Optional<AuditLog> findById(long id) throws InterruptedException {
        Thread.sleep(2000);
        return auditLogRepository.findById(id);
    }

    public List<AuditLog> findAll() throws InterruptedException {
        Thread.sleep(2000);
        return auditLogRepository.findAll();
    }

    public void deleteById(long id) throws InterruptedException {
        Thread.sleep(2000);
        auditLogRepository.deleteById(id);
    }
}
