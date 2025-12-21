package com.vending.consumer.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vending_telemetry")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelemetryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String machineId;
    private LocalDateTime timestamp;
    private Double temperature;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String inventoryJson;

    private String status;
}
