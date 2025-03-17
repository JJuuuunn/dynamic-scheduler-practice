package com.jjuuuunn.dynamicscheduler.dummyChannel.model.request;

import com.jjuuuunn.dynamicscheduler.dummyChannel.model.entity.DummyChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record DummyChannelCreateRequest(
        @NotBlank(message = "deviceCode는 비어 있을 수 없습니다.")
        String deviceCode,

        @NotBlank(message = "channelCode는 비어 있을 수 없습니다.")
        String channelCode,

        @PositiveOrZero(message = "channelTypeId는 양수를 입력해야 합니다.")
        Short channelTypeId,

        @NotNull(message = "minValue는 비어 있을 수 없습니다.")
        Long minValue,

        @NotNull(message = "maxValue는 비어 있을 수 없습니다.")
        Long maxValue,

        @NotNull(message = "tenantId는 비어 있을 수 없습니다.")
        @Positive(message = "tenantId가 음수 일리가 없습니다.")
        Integer tenantId,

        @NotBlank(message = "platformCode는 비어 있을 수 없습니다.")
        String platformCode,

        @Positive(message = "주기는 양수를 입력해야 합니다.")
        Long cycle,

        @NotBlank(message = "kafkaTopic는 비어 있을 수 없습니다.")
        String kafkaTopic
) {
    public DummyChannel toEntity() {
        return DummyChannel.builder()
                .deviceCode(deviceCode)
                .channelCode(channelCode)
                .channelTypeId(channelTypeId != null ? channelTypeId : (short) 0)
                .minValue(minValue)
                .maxValue(maxValue)
                .tenantId(tenantId)
                .platformCode(platformCode)
                .cycle(cycle != null ? cycle : 1L)
                .kafkaTopic(kafkaTopic)
                .build();
    }
}
