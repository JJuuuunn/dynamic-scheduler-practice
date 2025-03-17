package com.jjuuuunn.dynamicscheduler.dummyChannel.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@ToString
@Getter
@Entity
@Builder
@Table(name = "dummy_channel")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DummyChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("디바이스 이름")
    @Column(name = "device_code", nullable = false)
    private String deviceCode;

    @Comment("채널 이름")
    @Column(name = "channel_code", nullable = false)
    private String channelCode;

    @Comment("채널 데이터 종류별 seq(order)")
    @Column(name = "channel_type_id", nullable = false)
    private Short channelTypeId;

    @Comment("최소 값")
    @Column(name = "min_value")
    private Long minValue;

    @Comment("최대 값")
    @Column(name = "max_value")
    private Long maxValue;

    @Comment("테넌트 값")
    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;

    @Comment("플랫폼 이름")
    @Column(name = "platform_code", nullable = false)
    private String platformCode;

    @Comment("생성 주기")
    @Column(name = "cycle", nullable = false)
    private Long cycle;

    @Comment("Kafka Topic")
    @Column(name = "kafka_topic", nullable = false)
    private String kafkaTopic;
}

// CREATE TABLE dummy_channel (
//     id                   BIGINT          auto_increment primary key,
//     device_code          VARCHAR(255)    NOT NULL comment '디바이스 이름',
//     channel_code         VARCHAR(255)    NOT NULL comment '채널 이름',
//     min_value            BIGINT          NOT NULL comment '최소 값',
//     max_value            BIGINT          NOT NULL comment '최대 값',
//     tenant_id            INT             NOT NULL comment '테넌트 값',
//     platform_code        VARCHAR(255)    NOT NULL comment '플랫폼 이름',
//     channel_type_id      SMALLINT        NOT NULL comment '채널 데이터 종류별 seq(order)',
//     cycle                BIGINT          NOT NULL comment '생성 주기'
//     kafka_topic          VARCHAR(255)    NOT NULL comment 'Kafka Topic'
// ) comment '더미 데이터 동적 생성';
