package com.jjuuuunn.dynamicscheduler.dummyChannel.service;

import com.jjuuuunn.dynamicscheduler.dummyChannel.component.DummyChannelScheduler;
import com.jjuuuunn.dynamicscheduler.dummyChannel.model.request.DummyChannelCreateRequest;
import com.jjuuuunn.dynamicscheduler.dummyChannel.model.request.DummyChannelDeleteRequest;
import com.jjuuuunn.dynamicscheduler.dummyChannel.model.dto.RunningSchedulerCountDto;
import com.jjuuuunn.dynamicscheduler.dummyChannel.model.response.RunningSchedulerCountListResponse;
import com.jjuuuunn.dynamicscheduler.dummyChannel.model.entity.DummyChannel;
import com.jjuuuunn.dynamicscheduler.dummyChannel.repository.DummyChannelRepository;
import com.jjuuuunn.dynamicscheduler.dummyChannel.repository.DummyChannelStore;
import com.jjuuuunn.dynamicscheduler.infra.exception.CustomException;
import com.jjuuuunn.dynamicscheduler.infra.model.CommonMessageEnum;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * 더미 데이터 채널을 관리하는 서비스 클래스.
 * 더미 데이터를 추가하거나 삭제하는 기능을 제공함.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DummyChannelService {
    private final DummyChannelRepository dummyChannelRepository;
    private final DummyChannelScheduler scheduler; // 스케줄러 주입
    private final DummyChannelStore dummyChannelStore;

    /**
     * 더미 데이터 채널 추가
     *
     * @param request 더미 데이터 추가 요청 DTO
     * @return 저장 결과 메시지
     */
    @Transactional
    public CommonMessageEnum addDummyChannel(DummyChannelCreateRequest request) {
        // 1. DTO를 Entity로 변환
        DummyChannel entity = request.toEntity();

        // 2. 중복 데이터 존재 여부 확인
        dummyChannelRepository.findByDeviceCodeAndChannelCodeAndChannelTypeIdAndTenantIdAndPlatformCode(
                request.deviceCode(),
                request.channelCode(),
                request.channelTypeId(),
                request.tenantId(),
                request.platformCode()
        ).ifPresent(dummyChannel -> {
            log.info("중복된 채널 데이터: {}", entity);
            throw new CustomException("중복된 채널 데이터입니다.");
        });

        // 3. DB에 데이터 저장
        dummyChannelRepository.save(entity);

        // 4. store에 데이터 추가
        dummyChannelStore.addChannel(entity);

        // 5. 스케줄러에 새로운 스케줄 등록
        scheduler.addNewSchedule(entity.getCycle());

        log.info("새로운 {}초 주기 스케줄 등록 완료", entity.getCycle());
        return CommonMessageEnum.SAVE;
    }

    /**
     * 더미 데이터 채널 삭제
     *
     * @param request 더미 데이터 삭제 요청 DTO
     * @return 삭제 결과 메시지
     */
    @Transactional
    public CommonMessageEnum deleteDummyChannel(DummyChannelDeleteRequest request) {
        // 1. 삭제할 데이터 조회
        DummyChannel dummyChannel = dummyChannelRepository.findByDeviceCodeAndChannelCodeAndChannelTypeIdAndTenantIdAndPlatformCode(
                request.deviceCode(),
                request.channelCode(),
                request.channelTypeId(),
                request.tenantId(),
                request.platformCode()
        ).orElseThrow(() -> {
            log.warn("There is no Dummy Channel to delete : {}", request);
            throw new NoResultException("더미 채널이 존재하지 않습니다.");
        });

        // 2. DB에 데이터 삭제
        dummyChannelRepository.delete(dummyChannel);

        // 3. store에 데이터 제거
        dummyChannelStore.removeChannel(dummyChannel);

        // 4. 스케줄러에서 데이터 제거
        scheduler.removeSchedule(dummyChannel.getCycle());

        return CommonMessageEnum.DELETE;
    }

    /**
     * 실행 중인 스케줄러 목록 조회
     *
     * @return 실행 중인 스케줄러 목록
     */
    public RunningSchedulerCountListResponse getRunningSchedulerList() {
        // 1. 스케줄러에서 실행 중인 스케줄 목록 조회
        Map<Long, ScheduledFuture<?>> scheduledTasks = scheduler.getScheduledTasks();

        // 2. 스케줄러 목록을 DTO로 변환
        List<RunningSchedulerCountDto> runningSchedulerCountDtoList = scheduledTasks.entrySet().stream()
                .map(entry -> {
                    Long cycle = entry.getKey();
                    int taskCount = dummyChannelStore.getAllChannel().get(cycle).size();

                    return RunningSchedulerCountDto.builder()
                            .cycle(cycle)
                            .taskCount(taskCount)
                            .build();
                })
                .toList();

        return RunningSchedulerCountListResponse.builder()
                .runningSchedulerList(runningSchedulerCountDtoList)
                .build();
    }
}