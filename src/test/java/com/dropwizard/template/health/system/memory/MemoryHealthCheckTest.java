package com.dropwizard.template.health.system.memory;

import com.dropwizard.template.health.enums.HealthCheckStatusEnum;
import com.dropwizard.template.health.enums.ToleranceType;
import com.dropwizard.template.health.system.MetricTolerance;
import com.dropwizard.template.health.system.enums.Metric;
import com.dropwizard.template.health.system.enums.MemoryType;
import com.dropwizard.template.health.system.memory.IMemoryHealthCheck;
import com.dropwizard.template.health.system.memory.MemoryHealthCheck;
import com.dropwizard.template.health.system.memory.MemoryMetricTolerance;
import com.dropwizard.template.health.system.model.MemoryHealthCheckModel;
import com.dropwizard.template.health.model.ComponentHealthCheckModel;
import com.dropwizard.template.health.model.ComponentInfo;
import com.dropwizard.template.health.model.HealthCheckTolerance;
import com.google.common.collect.ImmutableList;
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
    public static final Long TOTAL_MEMORY = 100L;
    public static final Long FREE_MEMORY = 80L;

    public static final Double PASS_VALUE = 0.0;
    public static final Double WARN_VALUE = 0.0;
    public static final Double FAIL_VALUE = 0.0;

    public static final String TEST_COMPONENT_ID = "testComponentId";
    public static final String TEST_COMPONENT_NAME = "testComponentName";
    public static final String TEST_COMPONENT_TYPE = "testComponentType";
    public static final String EXPECTED_BAD_PARAMETER_CONSTRUCTOR_MESSAGE = "We must process a memory type";

    static String EXPECTED_VERSION = "1.0";
    static String EXPECTED_DESCRIPTION = "This is a metric that is used to track memory";

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

        when(memoryHealthCheck.getLastErrorMessage()).thenReturn("");
        when(memoryHealthCheck.getMemoryHealthCheck()).thenReturn(memoryHealthCheckModel);

        return memoryHealthCheck;
    }

    private static MemoryHealthCheckModel buildValidHealthCheckModel() {
        return MemoryHealthCheckModel.builder()
                .totalMemory(TOTAL_MEMORY)
                .freeMemory(FREE_MEMORY)
                .build();
    }

    private static MetricTolerance buildMetricToleranceBytes() {
        return new MemoryMetricTolerance(Metric.BYTES, buildHealthCheckTolerance());
    }

    private static MetricTolerance buildMetricTolerancePercentage() {
        return new MemoryMetricTolerance(Metric.PERCENTAGE, buildHealthCheckTolerance());
    }

    private static HealthCheckTolerance buildHealthCheckTolerance() {
        return HealthCheckTolerance.builder()
                .passValue(PASS_VALUE)
                .warnValue(WARN_VALUE)
                .failValue(FAIL_VALUE)
                .toleranceType(ToleranceType.GREATER_THAN)
                .build();
    }

    @Test
    public void invalidMemoryConstructorTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new MemoryHealthCheck(ImmutableList.of(),
                    ImmutableList.of(), buildComponentInfo(), buildMemoryHealthCheck());
        });

        String actualMessage = exception.getMessage();
        Assertions.assertEquals(EXPECTED_BAD_PARAMETER_CONSTRUCTOR_MESSAGE, actualMessage);
    }

    public static Object[][] memoryMetricNameDataPoint() {
        return new Object[][] {
                {ImmutableList.of(MemoryType.FREE_MEMORY), MemoryType.FREE_MEMORY.getValue()},
                {ImmutableList.of(MemoryType.UTILIZED_MEMORY), MemoryType.UTILIZED_MEMORY.getValue()},
                {ImmutableList.of(MemoryType.TOTAL_MEMORY), MemoryType.TOTAL_MEMORY.getValue()},

                {ImmutableList.of(MemoryType.FREE_MEMORY, MemoryType.UTILIZED_MEMORY), ""},
                {ImmutableList.of(MemoryType.FREE_MEMORY, MemoryType.TOTAL_MEMORY), ""},
                {ImmutableList.of(MemoryType.UTILIZED_MEMORY, MemoryType.TOTAL_MEMORY), ""},

                {ImmutableList.of(MemoryType.FREE_MEMORY, MemoryType.UTILIZED_MEMORY, MemoryType.TOTAL_MEMORY), ""},
        };
    }

    @ParameterizedTest(name = "{index} => memoryTypeList={0}, expectedMetricName={1}")
    @MethodSource("memoryMetricNameDataPoint")
    public void memoryMetricNameTest(List<MemoryType> memoryTypeList, String expectedMetricName) {
        MemoryHealthCheck memoryHealthCheck = new MemoryHealthCheck(memoryTypeList,
                ImmutableList.of(), buildComponentInfo(), buildMemoryHealthCheck());

        Assertions.assertEquals(EXPECTED_VERSION, memoryHealthCheck.getVersion());
        Assertions.assertEquals(EXPECTED_DESCRIPTION, memoryHealthCheck.getDescription());
        Assertions.assertEquals(expectedMetricName, memoryHealthCheck.getMetricName());
    }

    public static Object[][] memoryHealthCheckDataPoint() {
        return new Object[][] {
                {ImmutableList.of(MemoryType.FREE_MEMORY), ImmutableList.of(buildMetricToleranceBytes()),
                        ImmutableList.of(80.0), ImmutableList.of(Metric.BYTES)},
                {ImmutableList.of(MemoryType.FREE_MEMORY), ImmutableList.of(buildMetricTolerancePercentage()),
                        ImmutableList.of(80.0), ImmutableList.of(Metric.PERCENTAGE)},
                {ImmutableList.of(MemoryType.FREE_MEMORY), ImmutableList.of(buildMetricToleranceBytes(), buildMetricTolerancePercentage()),
                        ImmutableList.of(80.0, 80.0), ImmutableList.of(Metric.BYTES, Metric.PERCENTAGE)},

                {ImmutableList.of(MemoryType.UTILIZED_MEMORY), ImmutableList.of(buildMetricToleranceBytes()),
                        ImmutableList.of(20.0), ImmutableList.of(Metric.BYTES)},
                {ImmutableList.of(MemoryType.UTILIZED_MEMORY), ImmutableList.of(buildMetricTolerancePercentage()),
                        ImmutableList.of(20.0), ImmutableList.of(Metric.PERCENTAGE)},
                {ImmutableList.of(MemoryType.UTILIZED_MEMORY), ImmutableList.of(buildMetricToleranceBytes(), buildMetricTolerancePercentage()),
                        ImmutableList.of(20.0, 20.0), ImmutableList.of(Metric.BYTES, Metric.PERCENTAGE)},

                {ImmutableList.of(MemoryType.TOTAL_MEMORY), ImmutableList.of(buildMetricToleranceBytes()),
                        ImmutableList.of(100.0), ImmutableList.of(Metric.BYTES)},
                {ImmutableList.of(MemoryType.TOTAL_MEMORY), ImmutableList.of(buildMetricTolerancePercentage()),
                        ImmutableList.of(100.0), ImmutableList.of(Metric.PERCENTAGE)},
                {ImmutableList.of(MemoryType.TOTAL_MEMORY), ImmutableList.of(buildMetricToleranceBytes(), buildMetricTolerancePercentage()),
                        ImmutableList.of(100.0, 100.0), ImmutableList.of(Metric.BYTES, Metric.PERCENTAGE)},

                {ImmutableList.of(MemoryType.FREE_MEMORY, MemoryType.UTILIZED_MEMORY), ImmutableList.of(buildMetricToleranceBytes()),
                        ImmutableList.of(80.0, 20.0), ImmutableList.of(Metric.BYTES, Metric.BYTES)},
                {ImmutableList.of(MemoryType.FREE_MEMORY, MemoryType.UTILIZED_MEMORY), ImmutableList.of(buildMetricTolerancePercentage()),
                        ImmutableList.of(80.0, 20.0), ImmutableList.of(Metric.PERCENTAGE, Metric.PERCENTAGE)},
                {ImmutableList.of(MemoryType.FREE_MEMORY, MemoryType.UTILIZED_MEMORY), ImmutableList.of(buildMetricToleranceBytes(), buildMetricTolerancePercentage()),
                        ImmutableList.of(80.0, 80.0, 20.0, 20.0), ImmutableList.of(Metric.BYTES, Metric.PERCENTAGE, Metric.BYTES, Metric.PERCENTAGE)},
        };
    }

    @ParameterizedTest(name="{index}=> memoryTypeList={0}, metricToleranceList={1}, expectedMetricValue={2}, expectedMetric={3}")
    @MethodSource("memoryHealthCheckDataPoint")
    public void memoryHealthCheckTest(List<MemoryType> memoryTypeList,
                                      List<MetricTolerance> metricToleranceList,
                                      List<Double> expectedMetricValueList,
                                      List<Metric> expectedMetricList) {
        MemoryHealthCheck memoryHealthCheck = new MemoryHealthCheck(memoryTypeList,
                metricToleranceList, buildComponentInfo(), buildMemoryHealthCheck());
        assertMemoryHealthCheckComponentList(expectedMetricList, expectedMetricValueList, memoryHealthCheck);
    }

    private static void assertMemoryHealthCheckComponentList(List<Metric> metricList,
                                                             List<Double> expectedMetricValueList, MemoryHealthCheck memoryHealthCheck) {
        List<ComponentHealthCheckModel.Value> componentValueList = memoryHealthCheck.getComponentValues();

        Assertions.assertEquals(expectedMetricValueList.size(), componentValueList.size());
        for (int i = 0; i < componentValueList.size(); i ++) {
            Metric metric = metricList.get(i);
            Double expectedMetricValue = expectedMetricValueList.get(i);
            ComponentHealthCheckModel.Value cv = componentValueList.get(i);

            Assertions.assertEquals(TEST_COMPONENT_ID, cv.getComponentId());
            Assertions.assertEquals(TEST_COMPONENT_TYPE, cv.getComponentType());
            Assertions.assertEquals(metric.getValue(), cv.getMetricUnit());
            Assertions.assertEquals(expectedMetricValue, cv.getMetricValue());
            Assertions.assertEquals(HealthCheckStatusEnum.PASS, cv.getStatus());
        }
    }


}
