package com.dropwizard.template.health.system.memory;

import com.dropwizard.template.health.system.model.MemoryHealthCheckModel;

public interface IMemoryHealthCheck {
    String getLastErrorMessage();
    MemoryHealthCheckModel getMemoryHealthCheck();
}
