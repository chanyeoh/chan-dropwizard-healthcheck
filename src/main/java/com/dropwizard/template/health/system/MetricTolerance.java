package com.dropwizard.template.health.system;

import com.dropwizard.template.health.system.enums.Metric;
import com.dropwizard.template.health.model.HealthCheckTolerance;

import java.util.List;

public interface MetricTolerance {
    List<Metric> getMeasureableMetric();
    HealthCheckTolerance getMetricTolerance(Metric metric);
}
