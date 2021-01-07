package com.dropwizard.template.health;

import com.codahale.metrics.health.HealthCheck;
import com.dropwizard.template.health.enums.HealthCheckStatusEnum;
import com.dropwizard.template.health.enums.ToleranceType;
import com.dropwizard.template.health.model.ComponentHealthCheckModel;
import com.dropwizard.template.health.model.ComponentInfo;
import com.dropwizard.template.health.model.HealthCheckTolerance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChanDropWizardHealthCheckTest {
    public static final String TEST_METRIC_VALUE = "testMetricValue";
    static final String TEST_COMPONENT_NAME_STRING = "test";
    static final String TEST_COMPONENT_TYPE_STRING = "test";

    static final String TEST_VERSION = "1.0";
    static final String TEST_METRIC = "testMetric";
    static final String TEST_DESCRIPTION = "testDescription";

    static final String EXPECTED_HEALTH_METRIC_NAME = TEST_COMPONENT_NAME_STRING + ":" + TEST_METRIC;
    static final String EXPECTED_EMPTY_HEALTH_METRIC_NAME = TEST_COMPONENT_NAME_STRING;
    public static final String TEST_COMPONENT_ID = "testComponentId";
    public static final String TEST_COMPONENT_TYPE = "testComponentType";
    public static final String TEST_METRIC_UNIT = "testMetricUnit";
    public static final String TEST_OUTPUT = "testOutput";

    public static final HealthCheckStatusEnum PASS = HealthCheckStatusEnum.PASS;
    public static final HealthCheckStatusEnum WARN = HealthCheckStatusEnum.WARN;
    public static final HealthCheckStatusEnum FAIL = HealthCheckStatusEnum.FAIL;

    public static Object[][] metricTitleTest() {
        return new Object[][] {
                {false, EXPECTED_HEALTH_METRIC_NAME},
                {true, EXPECTED_EMPTY_HEALTH_METRIC_NAME},
        };
    }

    @ParameterizedTest(name="{index} => isEmpty={0}, expectedMetricName={1}")
    @MethodSource("metricTitleTest")
    public void getMetricTitleTest(boolean isEmpty, String expectedMetricName) {
        ChanDropWizardHealthCheck healthCheck = buildTestChanDropWizardHealthCheck(buildComponentPassValues(), isEmpty);
        Assertions.assertEquals(expectedMetricName, healthCheck.getMetricTitle());
    }

    public static Object[][] healthCheckResultTest() {
        return new Object[][] {
                {buildComponentFailValues(), false, HealthCheckStatusEnum.FAIL},
                {buildComponentWarnValues(), true, HealthCheckStatusEnum.WARN},
                {buildComponentPassValues(), true, HealthCheckStatusEnum.PASS}
        };
    }

    @ParameterizedTest()
    @MethodSource("healthCheckResultTest")
    public void getHealthCheckResultTest(List<ComponentHealthCheckModel.Value> healthCheckTolerance,
                                         boolean isExpectedHealthy,
                                         HealthCheckStatusEnum expectedHealthEnum) throws JsonProcessingException {
        ChanDropWizardHealthCheck healthCheck = buildTestChanDropWizardHealthCheck(healthCheckTolerance);
        HealthCheck.Result result = healthCheck.getHealthCheckResult();

        assertHealthCheckResult(isExpectedHealthy, expectedHealthEnum, result);
    }

    private void assertHealthCheckResult(boolean isExpectedHealthy,
                                         HealthCheckStatusEnum expectedHealthEnum,
                                         HealthCheck.Result result) {
        Map<String, Object> resultDetails = result.getDetails();
        Assertions.assertEquals(result.isHealthy(), isExpectedHealthy);
        Assertions.assertEquals(resultDetails.get("metricName"), TEST_METRIC);
        Assertions.assertEquals(resultDetails.get("description"), TEST_DESCRIPTION);
        Assertions.assertEquals(resultDetails.get("componentName"), TEST_COMPONENT_NAME_STRING);
        Assertions.assertEquals(resultDetails.get("version"), TEST_VERSION);

        List<Map<String, Object>> componentList = (List<Map<String, Object>>) resultDetails.get("componentValue");
        Assertions.assertEquals(1, componentList.size());

        Map<String, Object> componentInfo = componentList.get(0);
        Assertions.assertEquals(TEST_COMPONENT_ID, componentInfo.get("componentId"));
        Assertions.assertEquals(TEST_COMPONENT_TYPE, componentInfo.get("componentType"));
        Assertions.assertEquals(TEST_METRIC_VALUE, componentInfo.get("metricValue"));
        Assertions.assertEquals(TEST_METRIC_UNIT, componentInfo.get("metricUnit"));
        Assertions.assertEquals(expectedHealthEnum.getValue(), componentInfo.get("status"));
        Assertions.assertEquals(TEST_OUTPUT, componentInfo.get("output"));
    }

    private static ChanDropWizardHealthCheck buildTestChanDropWizardHealthCheck(
            List<ComponentHealthCheckModel.Value> mockComponentValues) {
        return buildTestChanDropWizardHealthCheck(mockComponentValues, false);
    }

    private static ChanDropWizardHealthCheck buildTestChanDropWizardHealthCheck(
            List<ComponentHealthCheckModel.Value> mockComponentValues, boolean isEmpty) {
        ComponentInfo componentInfo = buildTestComponentInfo();
        IHealthCheckInfo healthCheckInfo = buildIHealthCheckInfo(mockComponentValues, isEmpty);

        return new ChanDropWizardHealthCheck(componentInfo, healthCheckInfo);
    }

    private static ComponentInfo buildTestComponentInfo() {
        return ComponentInfo.builder()
                .componentName(TEST_COMPONENT_NAME_STRING)
                .componentId(UUID.randomUUID().toString())
                .componentType(TEST_COMPONENT_TYPE_STRING)
                .build();
    }

    private static IHealthCheckInfo buildIHealthCheckInfo(List<ComponentHealthCheckModel.Value> componentValues,
                                                          boolean isMetricEmpty) {
        IHealthCheckInfo healthCheckInfoMock = mock(IHealthCheckInfo.class);

        when(healthCheckInfoMock.getVersion()).thenReturn(TEST_VERSION);
        when(healthCheckInfoMock.getMetricName()).thenReturn(isMetricEmpty ? "" : TEST_METRIC);
        when(healthCheckInfoMock.getDescription()).thenReturn(TEST_DESCRIPTION);
        when(healthCheckInfoMock.getStatusTolerance()).thenReturn(buildHealthCheckTolerance());
        when(healthCheckInfoMock.getComponentValues()).thenReturn(componentValues);

        return healthCheckInfoMock;
    }

    private static List<ComponentHealthCheckModel.Value> buildComponentPassValues() {
        ComponentHealthCheckModel.Value healthCheckModelValue = ComponentHealthCheckModel.Value.builder()
                .componentId(TEST_COMPONENT_ID)
                .componentType(TEST_COMPONENT_TYPE)
                .metricValue(TEST_METRIC_VALUE)
                .metricUnit(TEST_METRIC_UNIT)
                .status(PASS)
                .time(new Date())
                .output(TEST_OUTPUT) // Print if there are any errors
                .build();

        return ImmutableList.of(
                healthCheckModelValue
        );
    }

    private static List<ComponentHealthCheckModel.Value> buildComponentWarnValues() {
        ComponentHealthCheckModel.Value healthCheckModelValue = ComponentHealthCheckModel.Value.builder()
                .componentId(TEST_COMPONENT_ID)
                .componentType(TEST_COMPONENT_TYPE)
                .metricValue(TEST_METRIC_VALUE)
                .metricUnit(TEST_METRIC_UNIT)
                .status(WARN)
                .time(new Date())
                .output(TEST_OUTPUT) // Print if there are any errors
                .build();

        return ImmutableList.of(
                healthCheckModelValue
        );
    }

    private static List<ComponentHealthCheckModel.Value> buildComponentFailValues() {
        ComponentHealthCheckModel.Value healthCheckModelValue = ComponentHealthCheckModel.Value.builder()
                .componentId(TEST_COMPONENT_ID)
                .componentType(TEST_COMPONENT_TYPE)
                .metricValue(TEST_METRIC_VALUE)
                .metricUnit(TEST_METRIC_UNIT)
                .status(FAIL)
                .time(new Date())
                .output(TEST_OUTPUT) // Print if there are any errors
                .build();

        return ImmutableList.of(
                healthCheckModelValue
        );
    }

    private static HealthCheckTolerance buildHealthCheckTolerance() {
        return HealthCheckTolerance.builder()
                .failValue(100.0)
                .warnValue(50.0)
                .passValue(0.0)
                .toleranceType(ToleranceType.LESS_THAN)
                .build();
    }
//
//    private static HealthCheckTolerance buildHealthCheckToleranceGreaterThan() {
//        return HealthCheckTolerance.builder()
//                .failValue(0.0)
//                .warnValue(50.0)
//                .passValue(100.0)
//                .toleranceType(ToleranceType.GREATER_THAN)
//                .build();
//    }
}
