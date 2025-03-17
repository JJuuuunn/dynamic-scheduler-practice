package com.jjuuuunn.dynamicscheduler.dummyChannel.model.dto;

import lombok.Builder;

@Builder
public record KafkaKeyDto(
        String platformCode,
        String deviceCode,
        Integer tenantId,
        String kafkaTopic
) {
}
