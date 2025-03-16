package com.jjuuuunn.dynamicscheduler.dummyChannel.controller;

import com.jjuuuunn.dynamicscheduler.dummyChannel.model.dto.DummyChannelCreateRequest;
import com.jjuuuunn.dynamicscheduler.dummyChannel.model.dto.DummyChannelDeleteRequest;
import com.jjuuuunn.dynamicscheduler.dummyChannel.service.DummyChannelService;
import com.jjuuuunn.dynamicscheduler.infra.model.CommonMessageEnum;
import com.jjuuuunn.dynamicscheduler.infra.model.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/dummyChannel")
@RequiredArgsConstructor
public class DummyChannelController {

    private final DummyChannelService dummyChannelService;

    @PostMapping
    public CustomResponse<CommonMessageEnum> addDummyDataChannel(
            @Schema(description = "Device, Channel, Order, 최소값, 최대값, TenantId, PlatformCode, 생성주기",
                    example = "{\"deviceCode\": \"device123\",\n" +
                            "\"channelCode\": \"code001\",\n" +
                            "\"channelTypeId\": 1,\n" +
                            "\"minValue\": 100,\n" +
                            "\"maxValue\": 200,\n" +
                            "\"tenantId\": 1,\n" +
                            "\"platformCode\": \"platform01\",\n" +
                            "\"generationInterval\": 3}")
            @RequestBody
            DummyChannelCreateRequest request) {
        log.info("DummyDataController - addDummyDataChannel : {}", request);
        return CustomResponse.ok(dummyChannelService.addDummyChannel(request));
    }


    @DeleteMapping
    @Operation(
            summary = "더미 데이터 채널 삭제 요청",
            description = "버킷 신규 등록 및 TenantId - Bucket 매핑 "
    )
    public CustomResponse<CommonMessageEnum> deleteDummyDataChannel(@RequestBody DummyChannelDeleteRequest request) {
        log.info("DummyDataController - deleteDummyDataChannel : {}", request);
        return CustomResponse.ok(dummyChannelService.deleteDummyChannel(request));
    }
}
