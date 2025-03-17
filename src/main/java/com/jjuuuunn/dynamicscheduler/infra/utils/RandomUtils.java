package com.jjuuuunn.dynamicscheduler.infra.utils;


import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtils {

    /**
     * 0.0 이상 1.0 미만의 랜덤한 실수를 생성하고, 소수 첫째 자리에서 반올림하여 반환합니다.
     * @return 0.0 이상 1.0 미만의 난수 (소수 첫째 자리까지 반올림)
     */
    public static double getDoubleRandom() {
        double randomValue = ThreadLocalRandom.current().nextDouble();
        return Math.round(randomValue * 10) / 10.0;
    }

    /**
     * 주어진 최소값과 최대값 범위 내에서 랜덤한 실수를 생성하고, 소수 첫째 자리에서 반올림하여 반환합니다.
     * @param min 최소값
     * @param max 최대값
     * @return min과 max 사이의 난수 (소수 첫째 자리까지 반올림)
     * @throws IllegalArgumentException min 값이 max 값보다 클 경우 예외 발생
     */
    public static double getDoubleRandom(Long min, Long max) {
        if (min >= max) {
            throw new IllegalArgumentException("min 값이 max 값보다 클 수 없습니다.");
        }
        double randomValue = ThreadLocalRandom.current().nextDouble(min, max);
        return Math.round(randomValue * 10) / 10.0;
    }
}
