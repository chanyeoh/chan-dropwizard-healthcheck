package com.dropwizard.template.health.system.memory;

import com.dropwizard.template.health.enums.HealthCheckStatusEnum;
import com.dropwizard.template.health.enums.ToleranceType;
import com.dropwizard.template.health.model.HealthCheckTolerance;
import com.dropwizard.template.health.system.enums.Metric;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MemoryMetricToleranceTest {
    static final String INVALID_LOWER_BOUND = "Minimum Value must be >= 0";
    static final String INVALID_UPPER_BOUND = "Percentage constraint is 0-100 %";

    public static Object[][] invalidLowerBoundData() {
        return new Object[][] {
                {Metric.BYTES, buildInvalidHealthCheckTolerance(-1.0, -2.0, -3.0, ToleranceType.GREATER_THAN)},
                {Metric.BYTES, buildInvalidHealthCheckTolerance(3.0, -2.0, -3.0, ToleranceType.GREATER_THAN)},
                {Metric.BYTES, buildInvalidHealthCheckTolerance(3.0, 2.0, -3.0, ToleranceType.GREATER_THAN)},
                {Metric.PERCENTAGE, buildInvalidHealthCheckTolerance(-1.0, -2.0, -3.0, ToleranceType.GREATER_THAN)},
                {Metric.PERCENTAGE, buildInvalidHealthCheckTolerance(3.0, -2.0, -3.0, ToleranceType.GREATER_THAN)},
                {Metric.PERCENTAGE, buildInvalidHealthCheckTolerance(3.0, 2.0, -3.0, ToleranceType.GREATER_THAN)},
        };
    }

    public static Object[][] invalidUpperBoundData() {
        return new Object[][] {
                {Metric.PERCENTAGE, buildInvalidHealthCheckTolerance(101.0, 102.0, 103.0, ToleranceType.LESS_THAN)},
                {Metric.PERCENTAGE, buildInvalidHealthCheckTolerance(99.0, 102.0, 103.0, ToleranceType.LESS_THAN)},
                {Metric.PERCENTAGE, buildInvalidHealthCheckTolerance(99.0, 100.0, 103.0, ToleranceType.LESS_THAN)},
        };
    }

    public static Object[][] validDataPoints() {
        return new Object[][] {
                {Metric.BYTES, buildInvalidHealthCheckTolerance(101.0, 102.0, 103.0, ToleranceType.LESS_THAN),
                        101.0, HealthCheckStatusEnum.PASS},
                {Metric.BYTES, buildInvalidHealthCheckTolerance(101.0, 102.0, 103.0, ToleranceType.LESS_THAN),
                        102.0, HealthCheckStatusEnum.WARN},
                {Metric.BYTES, buildInvalidHealthCheckTolerance(101.0, 102.0, 103.0, ToleranceType.LESS_THAN),
                        103.0, HealthCheckStatusEnum.FAIL},
                {Metric.PERCENTAGE, buildInvalidHealthCheckTolerance(0.0, 50.0, 100.0, ToleranceType.LESS_THAN),
                        0.0, HealthCheckStatusEnum.PASS},
                {Metric.PERCENTAGE, buildInvalidHealthCheckTolerance(0.0, 50.0, 100.0, ToleranceType.LESS_THAN),
                        50.0, HealthCheckStatusEnum.WARN},
                {Metric.PERCENTAGE, buildInvalidHealthCheckTolerance(0.0, 50.0, 100.0, ToleranceType.LESS_THAN),
                        100.0, HealthCheckStatusEnum.FAIL},
        };
    }

    private static HealthCheckTolerance buildInvalidHealthCheckTolerance(Double pass,
                                                                         Double warn, Double fail,
                                                                         ToleranceType toleranceType) {
        return HealthCheckTolerance.builder()
                .passValue(pass)
                .warnValue(warn)
                .failValue(fail)
                .toleranceType(toleranceType)
                .build();
    }

    @ParameterizedTest(name = "{index} => metric={0}, healthCheckTolerance={1}")
    @MethodSource("invalidLowerBoundData")
    public void invalidLowerBoundTest(Metric metric, HealthCheckTolerance healthCheckTolerance) {
        Exception exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            new MemoryMetricTolerance(metric, healthCheckTolerance);
        });

        String message = exception.getMessage();
        Assertions.assertEquals(INVALID_LOWER_BOUND, message);
    }

    @ParameterizedTest(name = "{index} => metric={0}, healthCheckTolerance={1}")
    @MethodSource("invalidUpperBoundData")
    public void invalidUpperBoundTest(Metric metric, HealthCheckTolerance healthCheckTolerance) {
        Exception exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            new MemoryMetricTolerance(metric, healthCheckTolerance);
        });

        String message = exception.getMessage();
        Assertions.assertEquals(INVALID_UPPER_BOUND, message);
    }

    @ParameterizedTest(name = "{index} => metric={0}, healthCheckTolerance={1}, value={2}, expectedHealthCheckEnum={3}")
    @MethodSource("validDataPoints")
    public void validMemoryMetricTest(Metric metric, HealthCheckTolerance healthCheckTolerance, Double value,
                                      HealthCheckStatusEnum expectedHealthCheckEnum) {
        MemoryMetricTolerance memoryMetricTolerance = new MemoryMetricTolerance(metric, healthCheckTolerance);
        HealthCheckStatusEnum healthCheckStatusEnum = memoryMetricTolerance.getMetricHealthCheck(value);

        Assertions.assertEquals(metric, memoryMetricTolerance.getMetric());
        Assertions.assertEquals(healthCheckStatusEnum, expectedHealthCheckEnum);
    }
}
