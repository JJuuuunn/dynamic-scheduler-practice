package com.jjuuuunn.dynamicscheduler.dummyChannel.model.dto;

public record DummyChannelDeleteRequest(
        String deviceCode,
        String channelCode,
        Short channelTypeId,
        Integer tenantId,
        String platformCode
) {
}
