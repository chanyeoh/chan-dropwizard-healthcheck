package com.dropwizard.template.health.model;

import com.dropwizard.template.health.MemoryHealthCheckUtil;
import com.dropwizard.template.health.system.model.MemoryHealthCheckModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MemoryHealthCheckModelTest {
    public static Object[][] validMemoryDataPoints() {
        return new Object[][] {
                {100L, 0L, 100L, 0.0, 100.0}
        };
    }

    @ParameterizedTest(name="{index} => totalMemory={0}, freeMemory={1}, " +
            "expectedUtilizedMemory={2}, expectedFreeMemoryPercentage={3}, " +
            "expectedUtilizedMemoryPercentage={4}")
    @MethodSource("validMemoryDataPoints")
    public void testValidMemoryData(Long totalMemory, Long freeMemory,
                                    Long expectedUtilizedMemory, Double expectedFreeMemoryPercentage,
                                    Double expectedUtilizedMemoryPercentage) {
        MemoryHealthCheckModel memoryHealthCheckModel =
                buildMemoryHelthCheckModel(totalMemory, freeMemory);

        MemoryHealthCheckUtil.assertHealthCheckModel(totalMemory, freeMemory, expectedUtilizedMemory,
                expectedFreeMemoryPercentage, expectedUtilizedMemoryPercentage, memoryHealthCheckModel);
    }

    public static Object[][] invalidMemoryDataPoints() {
        return new Object[][] {
                {0L, 0L},
                {0L, -1L},
                {-1L, -1L}
        };
    }

    @ParameterizedTest(name="{index} => totalMemory={0}, freeMemory={1}, " +
            "expectedUtilizedMemory={2}, expectedFreeMemoryPercentage={3}, " +
            "expectedUtilizedMemoryPercentage={4}")
    @MethodSource("invalidMemoryDataPoints")
    public void testInvalidMemoryData(Long totalMemory, Long freeMemory) {
        MemoryHealthCheckModel memoryHealthCheckModel =
                buildMemoryHelthCheckModel(totalMemory, freeMemory);
        MemoryHealthCheckUtil.assertInvalidHealthCheckModel(totalMemory, freeMemory, memoryHealthCheckModel);
    }

    @Test
    public void testEmptyMemoryData() {
        MemoryHealthCheckModel memoryHealthCheckModel = MemoryHealthCheckModel.empty();
        MemoryHealthCheckUtil.assertInvalidHealthCheckModel(-1L, -1L, memoryHealthCheckModel);
    }

    private MemoryHealthCheckModel buildMemoryHelthCheckModel(Long totalMemory, Long freeMemory) {
        MemoryHealthCheckModel.MemoryHealthCheckModelBuilder builder =
                MemoryHealthCheckModel.builder();
        builder.totalMemory(totalMemory);
        builder.freeMemory(freeMemory);
        return builder.build();
    }
}
