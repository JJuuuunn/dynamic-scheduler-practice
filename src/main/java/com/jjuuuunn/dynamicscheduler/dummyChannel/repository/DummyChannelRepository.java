package com.jjuuuunn.dynamicscheduler.dummyChannel.repository;

import com.jjuuuunn.dynamicscheduler.dummyChannel.model.entity.DummyChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DummyChannelRepository extends JpaRepository<DummyChannel, Long> {
    Optional<DummyChannel> findByDeviceCodeAndChannelCodeAndChannelTypeIdAndTenantIdAndPlatformCode(
            String deviceCode,
            String channelCode,
            Short channelTypeId,
            Integer tenantId,
            String platformCode
    );
}
