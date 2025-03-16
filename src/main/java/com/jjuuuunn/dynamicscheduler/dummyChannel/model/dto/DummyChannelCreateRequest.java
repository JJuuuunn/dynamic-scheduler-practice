package com.jjuuuunn.dynamicscheduler.dummyChannel.model.dto;

import com.jjuuuunn.dynamicscheduler.dummyChannel.model.entity.DummyChannel;

public record DummyChannelCreateRequest(
        String deviceCode,
        String channelCode,
        Short channelTypeId,
        Long minValue,
        Long maxValue,
        Integer tenantId,
        String platformCode,
        Long generationInterval
) {
    public DummyChannel toEntity() {
        return DummyChannel.builder()
                .deviceCode(deviceCode)
                .channelCode(channelCode)
                .channelTypeId(channelTypeId)
                .minValue(minValue)
                .maxValue(maxValue)
                .tenantId(tenantId)
                .platformCode(platformCode)
                .generationInterval(generationInterval)
                .build();
    }
}
