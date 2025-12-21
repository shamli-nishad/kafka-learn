package com.vending.producer.controller;

import com.vending.producer.model.TelemetryData;
import com.vending.producer.service.TelemetryProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryProducerService producerService;

    @PostMapping
    public ResponseEntity<String> receiveTelemetry(@RequestBody TelemetryData data) {
        if (data.getTimestamp() == null) {
            data.setTimestamp(LocalDateTime.now());
        }
        producerService.sendTelemetry(data);
        return ResponseEntity.ok("Telemetry received and queued");
    }
}
