package com.dropwizard.template.health.system.enums;

import com.dropwizard.template.health.system.enums.MemoryType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MemoryTypeEnumTest {
    public static Object[][] testPointForHealthCheck() {
        return new Object[][]{
                {MemoryType.FREE_MEMORY, "freeMemory"},
                {MemoryType.TOTAL_MEMORY, "totalMemory"},
                {MemoryType.UTILIZED_MEMORY, "utilizedMemory"},
        };
    }

    @ParameterizedTest(name = "{index} => memoryType={0}, expectedString={1}")
    @MethodSource("testPointForHealthCheck")
    public void testOutputForHealthCheckEnum(MemoryType memoryType,
                                             String expectedString) {
        Assertions.assertEquals(expectedString, memoryType.getValue());
    }

}
