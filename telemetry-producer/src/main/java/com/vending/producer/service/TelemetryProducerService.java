package com.vending.producer.service;

import com.vending.producer.model.TelemetryData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelemetryProducerService {

    private static final String TOPIC = "vending-telemetry";
    private final KafkaTemplate<String, TelemetryData> kafkaTemplate;

    public void sendTelemetry(TelemetryData data) {
        log.info("Sending telemetry for machine: {}", data.getMachineId());
        kafkaTemplate.send(TOPIC, data.getMachineId(), data);
    }
}
