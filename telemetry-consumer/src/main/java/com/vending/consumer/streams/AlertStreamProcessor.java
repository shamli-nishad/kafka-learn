package com.vending.consumer.streams;

import com.vending.avro.TelemetryAvro;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.util.Collections;
import java.util.Map;

@Configuration
@EnableKafkaStreams
public class AlertStreamProcessor {

        @org.springframework.beans.factory.annotation.Value("${spring.kafka.properties.schema.registry.url}")
        private String schemaRegistryUrl;

        @Bean
        public KStream<String, TelemetryAvro> kStream(StreamsBuilder streamsBuilder) {
                // Serdes for Avro
                SpecificAvroSerde<TelemetryAvro> telemetrySerde = new SpecificAvroSerde<>();
                Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", schemaRegistryUrl);
                telemetrySerde.configure(serdeConfig, false);

                KStream<String, TelemetryAvro> stream = streamsBuilder.stream("vending-telemetry",
                                Consumed.with(Serdes.String(), telemetrySerde));

                // Filter for high temperature (> 10.0 degrees) and route to alert topic
                stream.filter((key, value) -> value.getTemperature() != null && value.getTemperature() > 10.0)
                                .peek((key, value) -> System.out.println(
                                                "STREAM ALERT: High temperature detected for machine " + key + ": "
                                                                + value.getTemperature()))
                                .to("telemetry-alerts", Produced.with(Serdes.String(), telemetrySerde));

                return stream;
        }
}
