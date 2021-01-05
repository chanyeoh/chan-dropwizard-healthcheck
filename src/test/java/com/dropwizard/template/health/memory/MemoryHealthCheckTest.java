package com.dropwizard.template.health.memory;

import com.dropwizard.template.health.MemoryHealthCheckUtil;
import com.dropwizard.template.health.memory.model.MemoryHealthCheckModel;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// https://gist.github.com/dimhold/5759950
@RunWith(PowerMockRunner.class)
@PrepareForTest(RuntimeMemoryHealthCheck.class)
public class MemoryHealthCheckTest {
    static final Long FREE_MEMORY_DATA = 800L;
    static final Long TOTAL_MEMORY_DATA = 1000L;
    static final Long UTILIZED_MEMORY_DATA = 200L;
    static final Double PERCENTAGE_FREE_MEMORY_DATA = 80.0;
    static final Double PERCENTAGE_TOTAL_MEMORY_DATA = 20.0;

    @Test
    public void validMemoryHealthCheck() {
        Runtime runtimeMock = mock(Runtime.class);
        when(runtimeMock.freeMemory()).thenReturn(FREE_MEMORY_DATA);
        when(runtimeMock.totalMemory()).thenReturn(TOTAL_MEMORY_DATA);

        PowerMockito.mockStatic(Runtime.class);
        PowerMockito.when(Runtime.getRuntime()).thenReturn(runtimeMock);

        RuntimeMemoryHealthCheck runtimeMemoryHealthCheck = new RuntimeMemoryHealthCheck();
        MemoryHealthCheckModel memoryHealthCheck = runtimeMemoryHealthCheck.getMemoryHealthCheck();
        assertHealthCheckModel(TOTAL_MEMORY_DATA, FREE_MEMORY_DATA, UTILIZED_MEMORY_DATA,
                PERCENTAGE_FREE_MEMORY_DATA, PERCENTAGE_TOTAL_MEMORY_DATA, memoryHealthCheck);
        Assertions.assertNull(runtimeMemoryHealthCheck.getLastErrorMessage());
    }

    @Test
    public void invalidTotalMemoryHealthCheck() {
        Runtime runtimeMock = mock(Runtime.class);
        when(runtimeMock.freeMemory()).thenReturn(FREE_MEMORY_DATA);
        when(runtimeMock.totalMemory()).thenThrow(IllegalArgumentException.class);

        PowerMockito.mockStatic(Runtime.class);
        PowerMockito.when(Runtime.getRuntime()).thenReturn(runtimeMock);

        RuntimeMemoryHealthCheck runtimeMemoryHealthCheck = new RuntimeMemoryHealthCheck();
        MemoryHealthCheckModel memoryHealthCheckModel = runtimeMemoryHealthCheck.getMemoryHealthCheck();
        MemoryHealthCheckUtil.assertInvalidHealthCheckModel(-1L, -1L, memoryHealthCheckModel);
        Assertions.assertNotNull(runtimeMemoryHealthCheck.getLastErrorMessage());
    }

    @Test
    public void invalidFreeMemoryHealthCheck() {
        Runtime runtimeMock = mock(Runtime.class);
        when(runtimeMock.freeMemory()).thenThrow(IllegalArgumentException.class);
        when(runtimeMock.totalMemory()).thenReturn(TOTAL_MEMORY_DATA);

        PowerMockito.mockStatic(Runtime.class);
        PowerMockito.when(Runtime.getRuntime()).thenReturn(runtimeMock);

        RuntimeMemoryHealthCheck runtimeMemoryHealthCheck = new RuntimeMemoryHealthCheck();
        MemoryHealthCheckModel memoryHealthCheckModel = runtimeMemoryHealthCheck.getMemoryHealthCheck();
        MemoryHealthCheckUtil.assertInvalidHealthCheckModel(-1L, -1L, memoryHealthCheckModel);
        Assertions.assertNotNull(runtimeMemoryHealthCheck.getLastErrorMessage());
    }

    @Test
    public void validThenInvalidThenValidMemoryHealthCheck() {
        validMemoryHealthCheck();
        invalidTotalMemoryHealthCheck();
        validMemoryHealthCheck();
    }

    @Test
    public void invalidThenValidThenInvalidMemoryHealthCheck() {
        invalidTotalMemoryHealthCheck();
        validMemoryHealthCheck();
        invalidTotalMemoryHealthCheck();
    }

    private void assertHealthCheckModel(Long totalMemory, Long freeMemory, Long expectedUtilizedMemory,
                                        Double expectedFreeMemoryPercentage, Double expectedUtilizedMemoryPercentage,
                                        MemoryHealthCheckModel memoryHealthCheckModel) {
        Assertions.assertEquals(totalMemory, memoryHealthCheckModel.getTotalMemory());
        Assertions.assertEquals(freeMemory, memoryHealthCheckModel.getFreeMemory());
        Assertions.assertEquals(expectedUtilizedMemory, memoryHealthCheckModel.getUtilizedMemory());
        Assertions.assertEquals(expectedFreeMemoryPercentage, memoryHealthCheckModel.getFreeMemoryPercentage());
        Assertions.assertEquals(expectedUtilizedMemoryPercentage, memoryHealthCheckModel.getUtilizedMemoryPercentage());
    }
}
