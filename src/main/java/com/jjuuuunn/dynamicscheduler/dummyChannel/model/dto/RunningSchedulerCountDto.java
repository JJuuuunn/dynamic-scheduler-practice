package com.jjuuuunn.dynamicscheduler.dummyChannel.model.dto;

import lombok.Builder;

@Builder
public record RunningSchedulerCountDto(
        Long period,
        int taskCount
) {
}
