package com.dropwizard.template.health;

import com.dropwizard.template.health.model.ComponentHealthCheckModel;
import com.dropwizard.template.health.model.HealthCheckTolerance;

import java.util.List;

public interface IHealthCheckInfo {

    String getVersion();
    String getDescription();
    String getMetricName();

    List<ComponentHealthCheckModel.Value> getComponentValues();
}
