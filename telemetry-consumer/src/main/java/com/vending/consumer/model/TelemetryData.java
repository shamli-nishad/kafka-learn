package com.vending.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelemetryData {
    private String machineId;
    private LocalDateTime timestamp;
    private Double temperature;
    private Map<String, Integer> inventory;
    private String status;
}
