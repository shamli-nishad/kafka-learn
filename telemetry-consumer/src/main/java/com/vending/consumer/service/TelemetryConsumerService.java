package com.vending.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vending.avro.TelemetryAvro;
import com.vending.consumer.entity.TelemetryEntity;
import com.vending.consumer.repository.TelemetryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
    public void consumeTelemetry(TelemetryAvro data, Acknowledgment ack) {
        log.info("Received telemetry via Avro for machine: {}", data.getMachineId());

        try {
            TelemetryEntity entity = new TelemetryEntity();
            entity.setMachineId(data.getMachineId().toString());

            // Convert Avro timestamp (Instant) to LocalDateTime
            LocalDateTime ts = LocalDateTime.ofInstant(data.getTimestamp(), ZoneId.systemDefault());
            entity.setTimestamp(ts);

            entity.setTemperature(data.getTemperature());
            entity.setStatus(data.getStatus().toString());

            // Convert inventory map to JSON string for storage
            String inventoryJson = objectMapper.writeValueAsString(data.getInventory());
            entity.setInventoryJson(inventoryJson);

            repository.save(entity);

            // Manually acknowledge the message
            ack.acknowledge();
            log.info("Persisted telemetry and acknowledged offset for machine: {}", data.getMachineId());
        } catch (JsonProcessingException e) {
            log.error("Error serializing inventory for machine: {}", data.getMachineId(), e);
            // We throw the exception so that the DefaultErrorHandler can handle retries/DLT
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Unexpected error processing telemetry for machine: {}", data.getMachineId(), e);
            throw e;
        }
    }
}
