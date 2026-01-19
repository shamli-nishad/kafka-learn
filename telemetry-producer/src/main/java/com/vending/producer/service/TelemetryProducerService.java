package com.vending.producer.service;

import com.vending.avro.TelemetryAvro;
import com.vending.producer.model.TelemetryData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
@org.springframework.transaction.annotation.Transactional
public class TelemetryProducerService {

    private static final String TOPIC = "vending-telemetry";
    private final KafkaTemplate<String, TelemetryAvro> kafkaTemplate;

    public void sendTelemetry(TelemetryData data) {
        TelemetryAvro avroData = TelemetryAvro.newBuilder()
                .setMachineId(data.getMachineId())
                .setTimestamp(data.getTimestamp().atZone(ZoneId.systemDefault()).toInstant())
                .setTemperature(data.getTemperature())
                .setInventory(data.getInventory())
                .setStatus(data.getStatus())
                .build();

        log.info("Sending telemetry via Avro for machine: {}", data.getMachineId());

        ListenableFuture<SendResult<String, TelemetryAvro>> future = kafkaTemplate.send(TOPIC, data.getMachineId(),
                avroData);

        future.addCallback(new ListenableFutureCallback<SendResult<String, TelemetryAvro>>() {
            @Override
            public void onSuccess(SendResult<String, TelemetryAvro> result) {
                log.info("Successfully sent message for machine: {} with offset: {}",
                        data.getMachineId(), result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message for machine: {}", data.getMachineId(), ex);
            }
        });
    }
}
