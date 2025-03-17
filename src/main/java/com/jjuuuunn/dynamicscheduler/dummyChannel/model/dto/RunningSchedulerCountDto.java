package com.jjuuuunn.dynamicscheduler.dummyChannel.model.dto;

import lombok.Builder;

@Builder
public record RunningSchedulerCountDto(
        Long cycle,
        int taskCount
) {
}
