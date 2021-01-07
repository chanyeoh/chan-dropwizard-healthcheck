package com.dropwizard.template.health.memory;

import com.dropwizard.template.health.enums.ToleranceType;
import com.dropwizard.template.health.memory.enums.MemoryMetric;
import com.dropwizard.template.health.memory.enums.MemoryType;
import com.dropwizard.template.health.memory.model.MemoryHealthCheckModel;
import com.dropwizard.template.health.model.HealthCheckTolerance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.dropwizard.template.health.MemoryHealthCheckUtil.assertHealthCheckTolerance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MemoryHealthCheckTest {
    public static final Long TOTAL_MEMORY = 1000L;
    public static final Long FREE_MEMORY = 800L;
    public static final Long UTILIZED_MEMORY = TOTAL_MEMORY - FREE_MEMORY;

    public static final Double TOTAL_MEMORY_PERC = 100.0;
    public static final Double FREE_MEMORY_PERC = 80.0;
    public static final Double UTILIZED_MEMORY_PERC = TOTAL_MEMORY_PERC - FREE_MEMORY_PERC;

    public static final Double PASS_VALUE = 1000.0;
    public static final Double WARN_VALUE = 500.0;
    public static final Double FAIL_VALUE = 0.0;

    public static final Double PASS_VALUE_PERC = 100.0;
    public static final Double WARN_VALUE_PERC = 50.0;
    public static final Double FAIL_VALUE_PERC = 0.0;

    static String EXPECTED_VERSION = "1.0";
    static String EXPECTED_DESCRIPTION = "This is a metric that is used to track memory";

    public static Object[][] memoryBytesDataPoint() {
        return new Object[][] {
                {MemoryType.FREE_MEMORY, MemoryType.FREE_MEMORY.getValue(), Double.valueOf(FREE_MEMORY)},
                {MemoryType.UTILIZED_MEMORY, MemoryType.UTILIZED_MEMORY.getValue(), Double.valueOf(UTILIZED_MEMORY)},
                {MemoryType.TOTAL_MEMORY, MemoryType.TOTAL_MEMORY.getValue(), Double.valueOf(TOTAL_MEMORY)},
        };
    }

    @ParameterizedTest(name="{index}=> memoryType={0}, expectedMetricName={1}, expectedMetric={2}")
    @MethodSource("memoryBytesDataPoint")
    public void memoryHealthCheckBytesTest(MemoryType memoryType, String expectedMetricName, Double expectedMetric) {
        MemoryHealthCheck memoryHealthCheck = new MemoryHealthCheck(memoryType,
                MemoryMetric.BYTES, buildMemoryHealthCheck(), buildHealthCheckToleranceBytes());

        assertMemoryHealthCheck(expectedMetricName, expectedMetric, memoryHealthCheck);
        assertHealthCheckTolerance(PASS_VALUE, WARN_VALUE, FAIL_VALUE,
                ToleranceType.GREATER_THAN, memoryHealthCheck.getStatusTolerance());
    }

    public static Object[][] memoryPercentageDataPoint() {
        return new Object[][] {
                {MemoryType.FREE_MEMORY, MemoryType.FREE_MEMORY.getValue(), FREE_MEMORY_PERC},
                {MemoryType.UTILIZED_MEMORY, MemoryType.UTILIZED_MEMORY.getValue(), UTILIZED_MEMORY_PERC},
                {MemoryType.TOTAL_MEMORY, MemoryType.TOTAL_MEMORY.getValue(), TOTAL_MEMORY_PERC},
        };
    }

    @ParameterizedTest(name="{index}=> memoryType={0}, expectedMetricName={1}, expectedMetric={2}")
    @MethodSource("memoryPercentageDataPoint")
    public void memoryHealthCheckPercentageTest(MemoryType memoryType, String expectedMetricName, Double expectedMetric) {
        MemoryHealthCheck memoryHealthCheck = new MemoryHealthCheck(memoryType,
                MemoryMetric.PERCENTAGE, buildMemoryHealthCheck(), buildHealthCheckTolerancePercentage());

        assertMemoryHealthCheck(expectedMetricName, expectedMetric, memoryHealthCheck);
        assertHealthCheckTolerance(PASS_VALUE_PERC, WARN_VALUE_PERC, FAIL_VALUE_PERC,
                ToleranceType.GREATER_THAN, memoryHealthCheck.getStatusTolerance());
    }

    private void assertMemoryHealthCheck(String expectedMetricName, Object expectedMetric, MemoryHealthCheck memoryHealthCheck) {
        Assertions.assertEquals(EXPECTED_VERSION, memoryHealthCheck.getVersion());
        Assertions.assertEquals(EXPECTED_DESCRIPTION, memoryHealthCheck.getDescription());
        Assertions.assertEquals(expectedMetricName, memoryHealthCheck.getMetricName());
//        Assertions.assertEquals(expectedMetric, memoryHealthCheck.getMetricValue());

    }

    private static IMemoryHealthCheck buildMemoryHealthCheck() {
        IMemoryHealthCheck memoryHealthCheck = mock(IMemoryHealthCheck.class);
        MemoryHealthCheckModel memoryHealthCheckModel = buildValidHealthCheckModel();

        when(memoryHealthCheck.getLastErrorMessage()).thenReturn(new Exception());
        when(memoryHealthCheck.getMemoryHealthCheck()).thenReturn(memoryHealthCheckModel);

        return memoryHealthCheck;
    }

    private static MemoryHealthCheckModel buildValidHealthCheckModel() {
        return MemoryHealthCheckModel.builder()
                .totalMemory(TOTAL_MEMORY)
                .freeMemory(FREE_MEMORY)
                .build();
    }

    private static HealthCheckTolerance buildHealthCheckToleranceBytes() {
        return HealthCheckTolerance.builder()
                .passValue(PASS_VALUE)
                .warnValue(WARN_VALUE)
                .failValue(FAIL_VALUE)
                .toleranceType(ToleranceType.GREATER_THAN)
                .build();
    }

    private static HealthCheckTolerance buildHealthCheckTolerancePercentage() {
        return HealthCheckTolerance.builder()
                .passValue(PASS_VALUE_PERC)
                .warnValue(WARN_VALUE_PERC)
                .failValue(FAIL_VALUE_PERC)
                .toleranceType(ToleranceType.GREATER_THAN)
                .build();
    }
}
