package com.jjuuuunn.dynamicscheduler.dummyChannel.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DummyChannelDeleteRequest(
        @NotBlank(message = "deviceCode는 비어 있을 수 없습니다.")
        String deviceCode,

        @NotBlank(message = "channelCode는 비어 있을 수 없습니다.")
        String channelCode,

        @NotNull(message = "channelTypeId는 비어 있을 수 없습니다.")
        Short channelTypeId,

        @NotNull(message = "tenantId는 비어 있을 수 없습니다.")
        @Positive(message = "tenantId가 음수 일리가 없습니다.")
        Integer tenantId,

        @NotBlank(message = "platformCode는 비어 있을 수 없습니다.")
        String platformCode

) {
}
