package com.jjuuuunn.dynamicscheduler.kafka.model;

import lombok.Builder;
import lombok.ToString;

import java.util.List;

@Builder
public record KafkaSMPDto (
        String platformCode,
        int tenantId,
        String deviceId,
        String version,
        long timestamp,
        List<Raw> data
) {
    @Builder
    public record Raw(
            String code,
            double value,
            short order
    ) {}
}

