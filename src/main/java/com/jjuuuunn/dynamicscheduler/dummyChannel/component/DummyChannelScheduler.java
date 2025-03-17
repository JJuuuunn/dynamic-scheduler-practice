package com.jjuuuunn.dynamicscheduler.dummyChannel.component;

import com.jjuuuunn.dynamicscheduler.dummyChannel.model.dto.KafkaKeyDto;
import com.jjuuuunn.dynamicscheduler.dummyChannel.model.entity.DummyChannel;
import com.jjuuuunn.dynamicscheduler.dummyChannel.repository.DummyChannelRepository;
import com.jjuuuunn.dynamicscheduler.dummyChannel.repository.DummyChannelStore;
import com.jjuuuunn.dynamicscheduler.infra.utils.RandomUtils;
import com.jjuuuunn.dynamicscheduler.kafka.component.KafkaProducer;
import com.jjuuuunn.dynamicscheduler.kafka.model.KafkaSMPDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private final KafkaProducer kafkaProducer;

    /**
     * 실행 중인 스케줄을 관리하는 맵 (Thread-Safe)
     */
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

        // 5. 각 주기에 대해 스케줄 등록
        dummyChannelStore.getAllChannel().keySet().forEach(this::addNewSchedule);

        log.info("Loaded {} Dummy Channel from DB & Set up a schedule.", dummyDataList.size());
    }

    /**
     * 특정 주기의 스케줄을 등록
     *
     * @param cycle 실행 주기 (초 단위)
     */
    public void addNewSchedule(Long cycle) {
        // 1. 이미 등록된 주기인지 확인
        if (scheduledTasks.containsKey(cycle)) {
            log.info("Already exists a {}-second cycle scheduler.", cycle);
            return;
        }

        // 2. 지정된 주기마다 실행되는 스케줄링 작업 추가
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(
                () -> executeTask(cycle),
                Duration.ofSeconds(cycle)
        );

        // 3. 실행 중인 스케줄 목록에 추가
        scheduledTasks.put(cycle, scheduledFuture);
        log.info("Added new {}-second cycle scheduler", cycle);
    }

    /**
     * 특정 주기(cycle)에 대한 작업을 실행
     * 현재는 데이터를 로그로 출력하며, 추후 Kafka 전송 로직으로 변경 예정
     *
     * @param cycle 실행 주기 (초 단위)
     */
    private void executeTask(Long cycle) {
        try {
            // 1. 주기별 데이터 조회
            List<DummyChannel> channelList = dummyChannelStore.getChannelByCycle(cycle);

            // 2. 데이터가 없는 경우, 종료
            if (channelList == null || channelList.isEmpty()) {
                log.warn("[{}초 주기] 실행 - 데이터 없음", cycle);
                return;
            }

            // 3. channelList를 platformCode, deviceCode, tenantId, kafkaTopic 으로 다시 그룹핑
            Map<KafkaKeyDto, List<DummyChannel>> map = channelList.stream().collect(Collectors.groupingBy(
                    channel -> KafkaKeyDto.builder()
                            .platformCode(channel.getPlatformCode())
                            .deviceCode(channel.getDeviceCode())
                            .tenantId(channel.getTenantId())
                            .kafkaTopic(channel.getKafkaTopic())
                            .build()
            ));

            map.forEach((key, value) -> {
                // 4. Kafka 전송을 위한 데이터 변환 및 더미 데이터 생성
                List<KafkaSMPDto.Raw> rawList = value.parallelStream()
                        .map(channel -> KafkaSMPDto.Raw.builder()
                                .code(channel.getChannelCode())
                                .value(RandomUtils.getDoubleRandom(channel.getMinValue(), channel.getMaxValue()))
                                .order(channel.getChannelTypeId())
                                .build())
                        .collect(Collectors.toList());

                // 5. Kafka 전송을 위한 데이터 생성
                KafkaSMPDto dto = KafkaSMPDto.builder()
                        .deviceId(key.deviceCode())
                        .timestamp(System.currentTimeMillis())
                        .platformCode(key.platformCode())
                        .tenantId(key.tenantId())
                        .version("1") // DB에 같이 저장할지는 고민 필요
                        .data(rawList)
                        .build();

                // 6. Kafka 전송
                kafkaProducer.send(key.kafkaTopic().toLowerCase(), dto);
                log.info("[{}초 주기] 실행 - 데이터: {}", cycle, dto.toString());
            });

        } catch (Exception e) {
            log.error("[{}초 주기] 실행 중 오류 발생: {}", cycle, e.getMessage(), e);
        }
    }

    /**
     * 특정 주기의 스케줄을 제거
     *
     * @param cycle 삭제할 주기 (초 단위)
     */
    public void removeSchedule(Long cycle) {
        // 1. 존재하는 주기인지 확인
        if (!scheduledTasks.containsKey(cycle)) {
            log.info("{}초 주기 스케줄러가 존재하지 않습니다.", cycle);
            return;
        }

        try {
            // 2. 실행 중인 스케줄 취소
            ScheduledFuture<?> future = scheduledTasks.get(cycle);
            if (future != null) { // 스케줄러가 존재하는 경우
                future.cancel(true);
                log.info("{}초 주기 스케줄러 취소 완료", cycle);
            }

            // 3. store 및 스케줄러 관리에서 제거
            dummyChannelStore.getAllChannel().remove(cycle);
            scheduledTasks.remove(cycle);
            log.info("{}초 주기 스케줄러 제거 완료", cycle);
        } catch (Exception e) {
            log.error("스케줄러 제거 중 오류 발생: {}", e.getMessage());
        }
    }
}
