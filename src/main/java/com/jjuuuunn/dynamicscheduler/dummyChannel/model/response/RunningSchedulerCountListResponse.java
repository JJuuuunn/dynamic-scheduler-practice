package com.jjuuuunn.dynamicscheduler.dummyChannel.model.response;

import com.jjuuuunn.dynamicscheduler.dummyChannel.model.dto.RunningSchedulerCountDto;
import lombok.Builder;

import java.util.List;

@Builder
public record RunningSchedulerCountListResponse(
        List<RunningSchedulerCountDto> runningSchedulerList
) {
}
