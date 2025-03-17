package com.jjuuuunn.dynamicscheduler.kafka.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class KafkaProducerInterceptor implements ProducerInterceptor<String, Object> {
    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> producerRecord) {
        log.info("Sending record => header: {}", producerRecord.headers());
        log.info("Sending record => topic: {}", producerRecord.topic());
        log.info("Sending record => value: {}", producerRecord.value());
        return producerRecord;
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
        if (e != null) {
            log.error("Kafka Sending Error: {}", e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        log.info("Kafka Closed");
    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
