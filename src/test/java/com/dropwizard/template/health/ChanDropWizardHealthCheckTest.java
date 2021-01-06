package com.dropwizard.template.health;

import com.codahale.metrics.health.HealthCheck;
import com.dropwizard.template.health.enums.ToleranceType;
import com.dropwizard.template.health.model.ComponentInfo;
import com.dropwizard.template.health.model.HealthCheckTolerance;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChanDropWizardHealthCheckTest {
    static final String TEST_COMPONENT_NAME_STRING = "test";
    static final String TEST_COMPONENT_TYPE_STRING = "test";

    static final String TEST_VERSION = "1.0";
    static final String TEST_METRIC = "testMetric";
    static final String TEST_DESCRIPTION = "testDescription";

    static final String EXPECTED_HEALTH_METRIC_NAME = TEST_COMPONENT_NAME_STRING + ":" + TEST_METRIC;

    @Test
    public void getMetricTitleTest() {
        ChanDropWizardHealthCheck healthCheck = buildTestChanDropWizardHealthCheck(0.0, buildHealthCheckToleranceLessThan());
        Assertions.assertEquals(EXPECTED_HEALTH_METRIC_NAME, healthCheck.getMetricTitle());
    }

    public static Object[][] healthCheckResultTest() {
        return new Object[][] {
                {0.0, buildHealthCheckToleranceGreaterThan(), false},
                {50.0, buildHealthCheckToleranceGreaterThan(), true},
                {100.0, buildHealthCheckToleranceGreaterThan(), true},
                {0.0, buildHealthCheckToleranceLessThan(), true},
                {50.0, buildHealthCheckToleranceLessThan(), true},
                {100.0, buildHealthCheckToleranceLessThan(), false},
        };
    }

    @ParameterizedTest()
    @MethodSource("healthCheckResultTest")
    public void getHealthCheckResultTest(Double metricValue,
                                         HealthCheckTolerance healthCheckTolerance,
                                         boolean isExpectedHealthy) throws JsonProcessingException {
        ChanDropWizardHealthCheck healthCheck = buildTestChanDropWizardHealthCheck(metricValue, healthCheckTolerance);
        HealthCheck.Result result = healthCheck.getHealthCheckResult();

        assertHealthCheckResult(isExpectedHealthy, result);
    }

    private void assertHealthCheckResult(boolean isExpectedHealthy, HealthCheck.Result result) {
        Map<String, Object> resultDetails = result.getDetails();
        Assertions.assertEquals(result.isHealthy(), isExpectedHealthy);
        Assertions.assertEquals(resultDetails.get("metricName"), TEST_METRIC);
        Assertions.assertEquals(resultDetails.get("description"), TEST_DESCRIPTION);
        Assertions.assertEquals(resultDetails.get("componentName"), TEST_COMPONENT_NAME_STRING);
        Assertions.assertEquals(resultDetails.get("version"), TEST_VERSION);
    }

    private static ChanDropWizardHealthCheck buildTestChanDropWizardHealthCheck(Double metricValue,
                                                                         HealthCheckTolerance healthCheckTolerance) {
        ComponentInfo componentInfo = buildTestComponentInfo();
        IHealthCheckInfo healthCheckInfo = buildIHealthCheckInfo(metricValue, healthCheckTolerance);

        return new ChanDropWizardHealthCheck(componentInfo, healthCheckInfo);
    }

    private static ComponentInfo buildTestComponentInfo() {
        return ComponentInfo.builder()
                .componentName(TEST_COMPONENT_NAME_STRING)
                .componentId(UUID.randomUUID().toString())
                .componentType(TEST_COMPONENT_TYPE_STRING)
                .build();
    }

    private static IHealthCheckInfo buildIHealthCheckInfo(Double metricValue,
                                                          HealthCheckTolerance healthCheckTolerance) {
        IHealthCheckInfo healthCheckInfoMock = mock(IHealthCheckInfo.class);

        when(healthCheckInfoMock.getVersion()).thenReturn(TEST_VERSION);
        when(healthCheckInfoMock.getMetricName()).thenReturn(TEST_METRIC);
        when(healthCheckInfoMock.getDescription()).thenReturn(TEST_DESCRIPTION);
        when(healthCheckInfoMock.getMetricValue()).thenReturn(metricValue);
        when(healthCheckInfoMock.getStatusTolerance()).thenReturn(healthCheckTolerance);

        return healthCheckInfoMock;
    }

    private static HealthCheckTolerance buildHealthCheckToleranceLessThan() {
        return HealthCheckTolerance.builder()
                .failValue(100.0)
                .warnValue(50.0)
                .passValue(0.0)
                .toleranceType(ToleranceType.LESS_THAN)
                .build();
    }

    private static HealthCheckTolerance buildHealthCheckToleranceGreaterThan() {
        return HealthCheckTolerance.builder()
                .failValue(0.0)
                .warnValue(50.0)
                .passValue(100.0)
                .toleranceType(ToleranceType.GREATER_THAN)
                .build();
    }
}
