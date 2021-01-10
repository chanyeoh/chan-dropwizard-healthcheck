package com.dropwizard.template.health;

import com.dropwizard.template.health.enums.ToleranceType;
import com.dropwizard.template.health.system.model.MemoryHealthCheckModel;
import com.dropwizard.template.health.model.HealthCheckTolerance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

public class MemoryHealthCheckUtil {
    static final String INVALID_MEMORY_ERROR_MESSAGE = "Total Memory must be > 0 and Free Memory must be >= 0";

    public static void assertHealthCheckTolerance(Double pass, Double warn, Double fail,
                                            ToleranceType toleranceType, HealthCheckTolerance healthCheckTolerance) {
        Assertions.assertEquals(pass, healthCheckTolerance.getPassValue());
        Assertions.assertEquals(warn, healthCheckTolerance.getWarnValue());
        Assertions.assertEquals(fail, healthCheckTolerance.getFailValue());
        Assertions.assertEquals(toleranceType, healthCheckTolerance.getToleranceType());
    }

    public static void assertHealthCheckModel(Long totalMemory, Long freeMemory, Long expectedUtilizedMemory,
                                        Double expectedFreeMemoryPercentage, Double expectedUtilizedMemoryPercentage,
                                        MemoryHealthCheckModel memoryHealthCheckModel) {
        Assertions.assertEquals(totalMemory, memoryHealthCheckModel.getTotalMemory());
        Assertions.assertEquals(freeMemory, memoryHealthCheckModel.getFreeMemory());
        Assertions.assertEquals(expectedUtilizedMemory, memoryHealthCheckModel.getUtilizedMemory());
        Assertions.assertEquals(expectedFreeMemoryPercentage, memoryHealthCheckModel.getFreeMemoryPercentage());
        Assertions.assertEquals(expectedUtilizedMemoryPercentage, memoryHealthCheckModel.getUtilizedMemoryPercentage());
    }

    public static void assertInvalidHealthCheckModel(Long totalMemory, Long freeMemory, MemoryHealthCheckModel memoryHealthCheckModel) {
        Assertions.assertEquals(totalMemory, memoryHealthCheckModel.getTotalMemory());
        Assertions.assertEquals(freeMemory, memoryHealthCheckModel.getFreeMemory());
        assertErrorMessage(INVALID_MEMORY_ERROR_MESSAGE, () ->
                memoryHealthCheckModel.getUtilizedMemory());
        assertErrorMessage(INVALID_MEMORY_ERROR_MESSAGE, () ->
                memoryHealthCheckModel.getFreeMemoryPercentage());
        assertErrorMessage(INVALID_MEMORY_ERROR_MESSAGE, () ->
                memoryHealthCheckModel.getUtilizedMemory());
    }

    public static void assertErrorMessage(String expectedErrorMessage, Executable executable) {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, executable);
        Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
