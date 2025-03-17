package com.jjuuuunn.dynamicscheduler.kafka.component;

import com.jjuuuunn.dynamicscheduler.kafka.model.KafkaSMPDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, KafkaSMPDto payload) {
        try {
            kafkaTemplate.send(topic, payload);
            log.info("Topic : {}, Payload={}", topic, payload);
        } catch (Exception e) {
            log.error("Kafka Producer Error: {}", e.getMessage());
        }
    }
}
