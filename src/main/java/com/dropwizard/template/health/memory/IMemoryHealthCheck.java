package com.dropwizard.template.health.memory;

import com.dropwizard.template.health.memory.model.MemoryHealthCheckModel;

public interface IMemoryHealthCheck {
    Exception getLastErrorMessage();
    MemoryHealthCheckModel getMemoryHealthCheck();
}
