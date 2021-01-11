package com.dropwizard.template.health.system.memory;

import com.dropwizard.template.health.enums.HealthCheckStatusEnum;
import com.dropwizard.template.health.model.HealthCheckTolerance;
import com.dropwizard.template.health.system.MetricTolerance;
import com.dropwizard.template.health.system.enums.Metric;
import org.junit.Assert;

public class MemoryMetricTolerance implements MetricTolerance {
    private Metric metric;
    private HealthCheckTolerance healthCheckTolerance;

    public MemoryMetricTolerance(Metric metric, HealthCheckTolerance healthCheckTolerance) {
        assertValidHealthCheckTolerance(metric, healthCheckTolerance);
        this.metric = metric;
        this.healthCheckTolerance = healthCheckTolerance;
    }

    private void assertValidHealthCheckTolerance(Metric metric, HealthCheckTolerance healthCheckTolerance) {
        assertValueLargerThanEqual0(healthCheckTolerance);
        if (metric == Metric.PERCENTAGE) {
            assertValueLessThanEqual100(healthCheckTolerance);
        }
    }

    private void assertValueLargerThanEqual0(HealthCheckTolerance healthCheckTolerance) {
        try {
            Assert.assertTrue(healthCheckTolerance.getPassValue() >= 0);
            Assert.assertTrue(healthCheckTolerance.getWarnValue() >= 0);
            Assert.assertTrue(healthCheckTolerance.getFailValue() >= 0);
        } catch (AssertionError e) {
            throw new IllegalArgumentException("Minimum Value must be >= 0");
        }
    }

    private void assertValueLessThanEqual100(HealthCheckTolerance healthCheckTolerance) {
        try {
            Assert.assertTrue(healthCheckTolerance.getPassValue() <= 100);
            Assert.assertTrue(healthCheckTolerance.getWarnValue() <= 100);
            Assert.assertTrue(healthCheckTolerance.getFailValue() <= 100);
        } catch (AssertionError e) {
            throw new IllegalArgumentException("Percentage constraint is 0-100 %");
        }
    }

    @Override
    public Metric getMetric() {
        return metric;
    }

    @Override
    public HealthCheckStatusEnum getMetricHealthCheck(Double value) {
        return healthCheckTolerance.getHealthCheckStatus(value);
    }
}
