package com.jjuuuunn.dynamicscheduler.dummyChannel.controller;

import com.jjuuuunn.dynamicscheduler.dummyChannel.model.response.RunningSchedulerCountListResponse;
import com.jjuuuunn.dynamicscheduler.dummyChannel.service.DummyChannelService;
import com.jjuuuunn.dynamicscheduler.infra.model.CustomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final DummyChannelService dummyChannelService;

    @GetMapping("/count")
    public CustomResponse<RunningSchedulerCountListResponse> getRunningSchedulerList() {
        log.info("DummyDataController - getAllDummyDataChannel");
        return CustomResponse.ok(dummyChannelService.getRunningSchedulerList());
    }
}
