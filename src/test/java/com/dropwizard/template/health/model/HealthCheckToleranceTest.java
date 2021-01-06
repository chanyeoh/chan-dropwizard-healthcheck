package com.dropwizard.template.health.model;

import com.dropwizard.template.health.enums.ToleranceType;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.dropwizard.template.health.MemoryHealthCheckUtil.assertHealthCheckTolerance;

public class HealthCheckToleranceTest {
    static final String INVALID_TOLERANCE_VALUE_ERROR_MESSAGE = "Invalid Tolerance Values";
    public static Object[][] validToleranceDefaultValues() {
        return new Object[][] {
                {0.0, 0.0, 0.0},
                {30.0, 30.0, 30.0},
                {10.0, 30.0, 30.0},
                {10.0, 30.0, 50.0},
        };
    }

    @ParameterizedTest(name = "{index} => pass={0}, warn={1}, fail={2}")
    @MethodSource("validToleranceDefaultValues")
    public void healthCheckToleranceDefaultTest(Double pass, Double warn, Double fail) {
        HealthCheckTolerance.HealthCheckToleranceBuilder healthCheckToleranceBuilder =
                HealthCheckTolerance.builder();
        healthCheckToleranceBuilder.passValue(pass);
        healthCheckToleranceBuilder.warnValue(warn);
        healthCheckToleranceBuilder.failValue(fail);

        HealthCheckTolerance healthCheckTolerance = healthCheckToleranceBuilder.build();
        assertHealthCheckTolerance(pass, warn, fail, ToleranceType.LESS_THAN, healthCheckTolerance);
    }

    public static Object[][] validToleranceValues() {
        return new Object[][] {
                {0.0, 0.0, 0.0, ToleranceType.LESS_THAN},
                {0.0, 0.0, 0.0, ToleranceType.GREATER_THAN},
                {30.0, 30.0, 30.0, ToleranceType.LESS_THAN},
                {30.0, 30.0, 30.0, ToleranceType.GREATER_THAN},
                {10.0, 30.0, 30.0, ToleranceType.LESS_THAN},
                {30.0, 30.0, 10.0, ToleranceType.GREATER_THAN},
                {10.0, 30.0, 50.0, ToleranceType.LESS_THAN},
                {50.0, 30.0, 10.0, ToleranceType.GREATER_THAN},
        };
    }

    @ParameterizedTest(name = "{index} => pass={0}, warn={1}, fail={2}, toleranceType={3}")
    @MethodSource("validToleranceValues")
    public void healthCheckToleranceTest(Double pass, Double warn, Double fail,
                                         ToleranceType toleranceType) {
        HealthCheckTolerance.HealthCheckToleranceBuilder healthCheckToleranceBuilder =
                HealthCheckTolerance.builder();
        healthCheckToleranceBuilder.passValue(pass);
        healthCheckToleranceBuilder.warnValue(warn);
        healthCheckToleranceBuilder.failValue(fail);
        healthCheckToleranceBuilder.toleranceType(toleranceType);

        HealthCheckTolerance healthCheckTolerance = healthCheckToleranceBuilder.build();
        assertHealthCheckTolerance(pass, warn, fail, toleranceType, healthCheckTolerance);
    }

    public static Object[][] invalidToleranceValues() {
        return new Object[][] {
                {10.0, 30.0, 30.0, ToleranceType.GREATER_THAN},
                {30.0, 30.0, 10.0, ToleranceType.LESS_THAN},
                {10.0, 30.0, 50.0, ToleranceType.GREATER_THAN},
                {50.0, 30.0, 10.0, ToleranceType.LESS_THAN},
        };
    }

    @ParameterizedTest(name = "{index} => pass={0}, warn={1}, fail={2}, toleranceType={3}")
    @MethodSource("invalidToleranceValues")
    public void healthCheckInvalidToleranceTest(Double pass, Double warn, Double fail,
                                         ToleranceType toleranceType) {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class , () -> {
            HealthCheckTolerance.HealthCheckToleranceBuilder healthCheckToleranceBuilder =
                    HealthCheckTolerance.builder();
            healthCheckToleranceBuilder.passValue(pass);
            healthCheckToleranceBuilder.warnValue(warn);
            healthCheckToleranceBuilder.failValue(fail);
            healthCheckToleranceBuilder.toleranceType(toleranceType);

            healthCheckToleranceBuilder.build();
        });

        String actualMessage = exception.getMessage();
        Assertions.assertTrue(StringUtils.containsIgnoreCase(actualMessage, INVALID_TOLERANCE_VALUE_ERROR_MESSAGE));
    }
}
