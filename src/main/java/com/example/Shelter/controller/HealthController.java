package com.example.Shelter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")  // убрал /api/
@Tag(name = "Здоровье системы", description = "Мониторинг состояния приложения")
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ApplicationAvailability availability;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Operation(summary = "Проверка работоспособности")
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Shelter Bot Application");

        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            health.put("database", "CONNECTED");
        } catch (Exception e) {
            health.put("database", "DISCONNECTED");
            health.put("databaseError", e.getMessage());
        }

        health.put("livenessState", availability.getLivenessState());
        health.put("readinessState", availability.getReadinessState());

        return ResponseEntity.ok(health);
    }

    @Operation(summary = "Проверка готовности")
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> readiness() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "READY");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Проверка жизнеспособности")
    @GetMapping("/live")
    public ResponseEntity<Map<String, Object>> liveness() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "LIVE");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Изменить состояние готовности")
    @PostMapping("/readiness/{state}")
    public ResponseEntity<String> changeReadinessState(@PathVariable String state) {
        if ("REFUSING_TRAFFIC".equalsIgnoreCase(state)) {
            AvailabilityChangeEvent.publish(eventPublisher, this, ReadinessState.REFUSING_TRAFFIC);
            return ResponseEntity.ok("Readiness state changed to REFUSING_TRAFFIC");
        } else if ("ACCEPTING_TRAFFIC".equalsIgnoreCase(state)) {
            AvailabilityChangeEvent.publish(eventPublisher, this, ReadinessState.ACCEPTING_TRAFFIC);
            return ResponseEntity.ok("Readiness state changed to ACCEPTING_TRAFFIC");
        }
        return ResponseEntity.badRequest().body("Invalid state. Use REFUSING_TRAFFIC or ACCEPTING_TRAFFIC");
    }

    @Operation(summary = "Информация о приложении")
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> appInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Shelter Bot");
        info.put("version", "1.0.0");
        info.put("description", "Telegram бот для приюта животных");
        info.put("environment", System.getenv("SPRING_PROFILES_ACTIVE"));
        info.put("javaVersion", System.getProperty("java.version"));
        return ResponseEntity.ok(info);
    }
}