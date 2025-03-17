package com.jjuuuunn.dynamicscheduler.dummyChannel.repository;

import com.jjuuuunn.dynamicscheduler.dummyChannel.model.entity.DummyChannel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 🛠️ 인메모리에서 더미 데이터를 관리하는 저장소 클래스
 */
@Component
public class DummyChannelStore {
    private final Map<Long, List<DummyChannel>> dataMap = new ConcurrentHashMap<>();

    /**
     * 저장된 전체 데이터 조회
     */
    public Map<Long, List<DummyChannel>> getAllChannel() {
        return dataMap;
    }

    /**
     * 모든 데이터를 삭제하고 초기화
     */
    public void clear() {
        dataMap.clear();
    }

    /**
     * 특정 주기에 대한 데이터 추가
     */
    public void addChannel(DummyChannel channel) {
        dataMap.computeIfAbsent(
                        channel.getCycle(), // Key(특정 주기)
                        k -> new CopyOnWriteArrayList<>() // Value 가 없는 경우, 새로운 리스트 생성
                )
                .add(channel);
    }

    /**
     * 특정 주기의 데이터 삭제
     */
    public void removeChannel(DummyChannel channel) {
        Long cycle = channel.getCycle();

        // 1. 특정 주기의 데이터가 없는 경우, 종료
        if (dataMap.containsKey(cycle)) {
            List<DummyChannel> dataList = dataMap.get(cycle);

            // 2. 특정 주기의 데이터 삭제
            dataList.remove(channel);
            if (dataList.isEmpty()) {
                dataMap.remove(cycle);
            }
        }
    }

    /**
     * 특정 주기의 데이터 가져오기
     */
    public List<DummyChannel> getChannelByCycle(Long cycle) {
        return dataMap.getOrDefault(cycle, new CopyOnWriteArrayList<>());
    }
}

