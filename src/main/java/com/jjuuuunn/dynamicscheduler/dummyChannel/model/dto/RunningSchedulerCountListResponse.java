package com.jjuuuunn.dynamicscheduler.dummyChannel.model.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RunningSchedulerCountListResponse(
        List<RunningSchedulerCountDto> runningSchedulerList
) {
}
