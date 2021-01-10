package com.dropwizard.template.health.system;

import com.dropwizard.template.health.enums.ToleranceType;
import com.dropwizard.template.health.system.enums.Metric;
import com.dropwizard.template.health.system.enums.MemoryType;
import com.dropwizard.template.health.system.memory.IMemoryHealthCheck;
import com.dropwizard.template.health.system.memory.MemoryHealthCheck;
import com.dropwizard.template.health.system.model.MemoryHealthCheckModel;
import com.dropwizard.template.health.model.ComponentHealthCheckModel;
import com.dropwizard.template.health.model.ComponentInfo;
import com.dropwizard.template.health.model.HealthCheckTolerance;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Getter;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MemoryHealthCheckTest {
    @Getter
    @Builder
    private static class ExpectedHealthCheckValue{
        private int componentValueSize;
        private String expectedMetricName;
        private Double expectedMetric;
        private Map<String, Double> expectedMeasurement;
    }

    public static final Long TOTAL_MEMORY = 1000L;
    public static final Long FREE_MEMORY = 800L;
    public static final Long UTILIZED_MEMORY = TOTAL_MEMORY - FREE_MEMORY;

    public static final Double TOTAL_MEMORY_PERC = 100.0;
    public static final Double FREE_MEMORY_PERC = 80.0;
    public static final Double UTILIZED_MEMORY_PERC = TOTAL_MEMORY_PERC - FREE_MEMORY_PERC;

    public static final Double PASS_VALUE = 1000.0;
    public static final Double WARN_VALUE = 0.0;
    public static final Double FAIL_VALUE = 0.0;

    public static final Double PASS_VALUE_PERC = 100.0;
    public static final Double WARN_VALUE_PERC = 0.0;
    public static final Double FAIL_VALUE_PERC = 0.0;

    public static final String TEST_COMPONENT_ID = "testComponentId";
    public static final String TEST_COMPONENT_NAME = "testComponentName";
    public static final String TEST_COMPONENT_TYPE = "testComponentType";
    public static final String EXPECTED_BAD_PARAMETER_CONSTRUCTOR_MESSAGE = "We must process a memory type";

    static String EXPECTED_VERSION = "1.0";
    static String EXPECTED_DESCRIPTION = "This is a metric that is used to track memory";

    @Test
    public void invalidMemoryConstructorTest() {
//        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            new MemoryHealthCheck(ImmutableList.of(),
//                    Metric.BYTES, buildComponentInfo(), buildMemoryHealthCheck(), buildHealthCheckToleranceBytes());
//        });
//
//        String actualMessage = exception.getMessage();
//        Assertions.assertEquals(EXPECTED_BAD_PARAMETER_CONSTRUCTOR_MESSAGE, actualMessage);
    }

    public static Object[][] memoryBytesDataPoint() {
        return new Object[][] {
                {ImmutableList.of(MemoryType.FREE_MEMORY), Metric.BYTES,
                        ExpectedHealthCheckValue.builder()
                                .expectedMetric(Double.valueOf(FREE_MEMORY))
                                .expectedMetricName(MemoryType.FREE_MEMORY.getValue())
                                .componentValueSize(1)
                                .expectedMeasurement(ImmutableMap.of(
                                        Metric.BYTES.getValue(), Double.valueOf(FREE_MEMORY)
                                ))
                                .build()},
                {ImmutableList.of(MemoryType.FREE_MEMORY), Metric.PERCENTAGE,
                        ExpectedHealthCheckValue.builder()
                                .expectedMetric(Double.valueOf(FREE_MEMORY))
                                .expectedMetricName(MemoryType.FREE_MEMORY.getValue())
                                .componentValueSize(1)
                                .expectedMeasurement(ImmutableMap.of(
                                        Metric.PERCENTAGE.getValue(), Double.valueOf(FREE_MEMORY_PERC)
                                ))
                            .build()},
                {ImmutableList.of(MemoryType.FREE_MEMORY), Metric.PERCENTAGE_BYTES,
                        ExpectedHealthCheckValue.builder()
                                .expectedMetric(Double.valueOf(FREE_MEMORY))
                                .expectedMetricName("")
                                .componentValueSize(2)
                                .expectedMeasurement(ImmutableMap.of(
                                        Metric.BYTES.getValue(), Double.valueOf(FREE_MEMORY),
                                        Metric.PERCENTAGE.getValue(), Double.valueOf(FREE_MEMORY_PERC)
                                ))
                            .build()},

                {ImmutableList.of(MemoryType.UTILIZED_MEMORY), Metric.BYTES,
                        ExpectedHealthCheckValue.builder()
                                .expectedMetric(Double.valueOf(UTILIZED_MEMORY))
                                .expectedMetricName(MemoryType.UTILIZED_MEMORY.getValue())
                                .componentValueSize(1)
                                .expectedMeasurement(ImmutableMap.of(
                                        Metric.BYTES.getValue(), Double.valueOf(UTILIZED_MEMORY)
                                ))
                            .build()},
                {ImmutableList.of(MemoryType.UTILIZED_MEMORY), Metric.PERCENTAGE,
                        ExpectedHealthCheckValue.builder()
                                .expectedMetric(Double.valueOf(UTILIZED_MEMORY))
                                .expectedMetricName(MemoryType.UTILIZED_MEMORY.getValue())
                                .componentValueSize(1)
                                .expectedMeasurement(ImmutableMap.of(
                                        Metric.PERCENTAGE.getValue(), Double.valueOf(UTILIZED_MEMORY_PERC)
                                ))
                                .build()},
                {ImmutableList.of(MemoryType.UTILIZED_MEMORY), Metric.PERCENTAGE_BYTES,
                        ExpectedHealthCheckValue.builder()
                                .expectedMetric(Double.valueOf(UTILIZED_MEMORY))
                                .expectedMetricName("")
                                .componentValueSize(2)
                                .expectedMeasurement(ImmutableMap.of(
                                        Metric.BYTES.getValue(), Double.valueOf(UTILIZED_MEMORY),
                                        Metric.PERCENTAGE.getValue(), Double.valueOf(UTILIZED_MEMORY_PERC)
                                ))
                                .build()},

                {ImmutableList.of(MemoryType.TOTAL_MEMORY), Metric.BYTES,
                        ExpectedHealthCheckValue.builder()
                                .expectedMetric(Double.valueOf(TOTAL_MEMORY))
                                .expectedMetricName(MemoryType.TOTAL_MEMORY.getValue())
                                .componentValueSize(1)
                                .expectedMeasurement(ImmutableMap.of(
                                        Metric.BYTES.getValue(), Double.valueOf(TOTAL_MEMORY)
                                ))
                                .build()},
                {ImmutableList.of(MemoryType.TOTAL_MEMORY), Metric.PERCENTAGE,
                        ExpectedHealthCheckValue.builder()
                                .expectedMetric(Double.valueOf(TOTAL_MEMORY))
                                .expectedMetricName(MemoryType.TOTAL_MEMORY.getValue())
                                .componentValueSize(1)
                                .expectedMeasurement(ImmutableMap.of(
                                        Metric.PERCENTAGE.getValue(), Double.valueOf(TOTAL_MEMORY_PERC)
                                ))
                                .build()},
                {ImmutableList.of(MemoryType.TOTAL_MEMORY), Metric.PERCENTAGE_BYTES,
                        ExpectedHealthCheckValue.builder()
                                .expectedMetric(Double.valueOf(TOTAL_MEMORY))
                                .expectedMetricName("")
                                .componentValueSize(2)
                                .expectedMeasurement(ImmutableMap.of(
                                        Metric.BYTES.getValue(), Double.valueOf(TOTAL_MEMORY),
                                        Metric.PERCENTAGE.getValue(), Double.valueOf(TOTAL_MEMORY_PERC)
                                ))
                                .build()},
        };
    }

    @ParameterizedTest(name="{index}=> memoryType={0}, memoryMetric={1}, expectedHealthCheckValue={2}")
    @MethodSource("memoryBytesDataPoint")
    public void memoryHealthCheckBytesTest(List<MemoryType> memoryTypeList, Metric metric,
                                           ExpectedHealthCheckValue expectedHealthCheckValue) {
//        MemoryHealthCheck memoryHealthCheck = new MemoryHealthCheck(memoryTypeList,
//                metric, buildComponentInfo(), buildMemoryHealthCheck(), buildHealthCheckToleranceBytes());
//
//        assertMemoryHealthCheck(expectedHealthCheckValue, memoryHealthCheck);
    }

    private static void assertMemoryHealthCheck(ExpectedHealthCheckValue expectedHealthCheckValue, MemoryHealthCheck memoryHealthCheck) {
        Assertions.assertEquals(EXPECTED_VERSION, memoryHealthCheck.getVersion());
        Assertions.assertEquals(EXPECTED_DESCRIPTION, memoryHealthCheck.getDescription());
        Assertions.assertEquals(expectedHealthCheckValue.getExpectedMetricName(), memoryHealthCheck.getMetricName());

        List<ComponentHealthCheckModel.Value> componentValues = memoryHealthCheck.getComponentValues();
        Assertions.assertEquals(expectedHealthCheckValue.getComponentValueSize(), componentValues.size());

        Set<String> metricUnitSet = new HashSet<>();
        for(ComponentHealthCheckModel.Value cv : componentValues) {
            Assertions.assertEquals(TEST_COMPONENT_ID, cv.getComponentId());
            Assertions.assertEquals(TEST_COMPONENT_TYPE, cv.getComponentType());

            String metricUnit = cv.getMetricUnit();
            metricUnitSet.add(metricUnit);

            Double metricValue = (Double) cv.getMetricValue();
            Double expectedMetricValue = expectedHealthCheckValue.getExpectedMeasurement().get(metricUnit);

            Assertions.assertEquals(expectedMetricValue, metricValue);
        }
        Assertions.assertEquals(expectedHealthCheckValue.getExpectedMeasurement().keySet(), metricUnitSet);
    }

    private static ComponentInfo buildComponentInfo() {
        ComponentInfo.ComponentInfoBuilder componentInfoBuilder = ComponentInfo.builder();
        componentInfoBuilder.componentId(TEST_COMPONENT_ID);
        componentInfoBuilder.componentName(TEST_COMPONENT_NAME);
        componentInfoBuilder.componentType(TEST_COMPONENT_TYPE);
        return componentInfoBuilder.build();
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
                .toleranceType(ToleranceType.LESS_THAN)
                .build();
    }

    private static HealthCheckTolerance buildHealthCheckTolerancePercentage() {
        return HealthCheckTolerance.builder()
                .passValue(PASS_VALUE_PERC)
                .warnValue(WARN_VALUE_PERC)
                .failValue(FAIL_VALUE_PERC)
                .toleranceType(ToleranceType.LESS_THAN)
                .build();
    }
}
