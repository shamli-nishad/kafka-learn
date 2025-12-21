package com.vending.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vending.consumer.entity.TelemetryEntity;
import com.vending.consumer.model.TelemetryData;
import com.vending.consumer.repository.TelemetryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TelemetryConsumerService {

    private final TelemetryRepository repository;
    private final ObjectMapper objectMapper;

    public TelemetryConsumerService(TelemetryRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "vending-telemetry", groupId = "telemetry-group")
    public void consumeTelemetry(TelemetryData data) {
        log.info("Received telemetry for machine: {}", data.getMachineId());

        try {
            TelemetryEntity entity = new TelemetryEntity();
            entity.setMachineId(data.getMachineId());
            entity.setTimestamp(data.getTimestamp());
            entity.setTemperature(data.getTemperature());
            entity.setStatus(data.getStatus());

            // Convert inventory map to JSON string for storage
            String inventoryJson = objectMapper.writeValueAsString(data.getInventory());
            entity.setInventoryJson(inventoryJson);

            repository.save(entity);
            log.info("Persisted telemetry to database for machine: {}", data.getMachineId());
        } catch (JsonProcessingException e) {
            log.error("Error serializing inventory for machine: {}", data.getMachineId(), e);
        }
    }
}
