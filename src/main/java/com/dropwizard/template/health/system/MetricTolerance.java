package com.dropwizard.template.health.system;

import com.dropwizard.template.health.enums.HealthCheckStatusEnum;
import com.dropwizard.template.health.system.enums.Metric;
import com.dropwizard.template.health.model.HealthCheckTolerance;

import java.util.List;

public interface MetricTolerance {
    Metric getMetric();
    HealthCheckStatusEnum getMetricHealthCheck(Double value);
}
