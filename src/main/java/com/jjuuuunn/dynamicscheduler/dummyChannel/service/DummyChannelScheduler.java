package com.jjuuuunn.dynamicscheduler.dummyChannel.service;

import com.jjuuuunn.dynamicscheduler.dummyChannel.model.entity.DummyChannel;
import com.jjuuuunn.dynamicscheduler.dummyChannel.repository.DummyChannelRepository;
import com.jjuuuunn.dynamicscheduler.dummyChannel.repository.DummyChannelStore;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * 더미 데이터의 주기적인 스케줄링을 관리하는 클래스.
 * 특정 주기의 데이터를 관리하고, 주어진 주기마다 실행되는 작업을 스케줄링함.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DummyChannelScheduler {
    private final ThreadPoolTaskScheduler taskScheduler;
    private final DummyChannelRepository dummyChannelRepository;
    private final DummyChannelStore dummyChannelStore;

    /** 실행 중인 스케줄을 관리하는 맵 (Thread-Safe) */
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();


    /**
     * Bean이 생성될 때 스케줄러를 초기화
     */
    @PostConstruct
    public void init() {
        taskScheduler.initialize();

        loadDataFromDB();
    }

    /**
     * 실행 중인 스케줄 목록 조회
     */
    public Map<Long, ScheduledFuture<?>> getScheduledTasks() {
        return scheduledTasks;
    }

    /**
     * DB에서 데이터를 로드하고, 주기별로 데이터를 그룹화하여 스케줄을 등록
     */
    private void loadDataFromDB() {

        // 1. DB에서 데이터 로드
        List<DummyChannel> dummyDataList = dummyChannelRepository.findAll();
        log.info("Load {} Dummy Channel from DB", dummyDataList.size());

        // 2. 데이터가 없는 경우, 종료
        if (dummyDataList.isEmpty()) {
            log.info("There is no Dummy Channel to load.");
            return;
        }

        // 3. dataMap 초기화
        dummyChannelStore.clear();
        log.info("Init dataMap: {}", dummyChannelStore.getAllChannel());

        // 4. 주기별로 데이터를 그룹화하여 저장
        dummyDataList.forEach(dummyChannelStore::addChannel);
//        dataMap.putAll(dummyDataList.stream()
//                .collect(Collectors.groupingBy(
//                        DummyChannel::getGenerationInterval,
//                        Collectors.toCollection(CopyOnWriteArrayList::new)
//                ))
//        );

        // 5. 각 주기에 대해 스케줄 등록
        dummyChannelStore.getAllChannel().keySet().forEach(this::addNewSchedule);
//        dataMap.keySet().forEach(this::addNewSchedule);

        log.info("Loaded {} Dummy Channel from DB & Set up a schedule.", dummyDataList.size());
    }

    /**
     * 특정 주기의 스케줄을 등록
     * @param period 실행 주기 (초 단위)
     */
    public void addNewSchedule(Long period) {
        // 1. 이미 등록된 주기인지 확인
        if (scheduledTasks.containsKey(period)) {
            log.info("Already exists a {}-second period scheduler.", period);
            return;
        }

        // 2. 지정된 주기마다 실행되는 스케줄링 작업 추가
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(
                () -> executeTask(period),
                Duration.ofSeconds(period)
        );

        // 3. 실행 중인 스케줄 목록에 추가
        scheduledTasks.put(period, scheduledFuture);
        log.info("Added new {}-second period scheduler", period);
    }

    /**
     * 특정 주기(period)에 대한 작업을 실행
     * 현재는 데이터를 로그로 출력하며, 추후 Kafka 전송 로직으로 변경 예정
     * TODO: Kafka 전송 로직 추가
     *
     * @param period 실행 주기 (초 단위)
     */
    private void executeTask(Long period) {
        try {
            // 1. 주기별 데이터 조회
            List<DummyChannel> channelList = dummyChannelStore.getChannelByPeriod(period);

            // 2. 데이터가 없는 경우, 종료
            if (channelList == null || channelList.isEmpty()) {
                log.warn("[{}초 주기] 실행 - 데이터 없음", period);
                return;
            }

            // 3. 데이터 로그 출력 (테스트용)
            // TODO: Kafka 전송 로직 추가
            log.info("[{}초 주기] 실행 - 데이터: {}", period, channelList);
        } catch (Exception e) {
            log.error("[{}초 주기] 실행 중 오류 발생: {}", period, e.getMessage(), e);
        }
    }

    /**
     * 특정 주기의 스케줄을 제거
     * @param period 삭제할 주기 (초 단위)
     */
    public void removeSchedule(Long period) {
        // 1. 존재하는 주기인지 확인
        if (!scheduledTasks.containsKey(period)) {
            log.info("{}초 주기 스케줄러가 존재하지 않습니다.", period);
            return;
        }

        try {
            // 2. 실행 중인 스케줄 취소
            ScheduledFuture<?> future = scheduledTasks.get(period);
            if (future != null) { // 스케줄러가 존재하는 경우
                future.cancel(true);
                log.info("{}초 주기 스케줄러 취소 완료", period);
            }

            // 3. store 및 스케줄러 관리에서 제거
            dummyChannelStore.getAllChannel().remove(period);
            scheduledTasks.remove(period);
            log.info("{}초 주기 스케줄러 제거 완료", period);
        } catch (Exception e) {
            log.error("스케줄러 제거 중 오류 발생: {}", e.getMessage());
        }
    }
}
