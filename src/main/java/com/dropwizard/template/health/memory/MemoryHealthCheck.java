package com.dropwizard.template.health.memory;

import com.dropwizard.template.health.IHealthCheckInfo;
import com.dropwizard.template.health.memory.enums.MemoryMetric;
import com.dropwizard.template.health.memory.enums.MemoryType;
import com.dropwizard.template.health.memory.model.MemoryHealthCheckModel;
import com.dropwizard.template.health.model.ComponentHealthCheckModel;
import com.dropwizard.template.health.model.HealthCheckTolerance;

import java.util.List;

public class MemoryHealthCheck implements IHealthCheckInfo {
    private MemoryType memoryType;
    private MemoryMetric memoryMetric;
    private IMemoryHealthCheck memoryHealthCheck;
    private HealthCheckTolerance healthCheckTolerance;

    static final String VERSION = "1.0";
    static final String DESCRIPTION = "This is a metric that is used to track memory";

    public MemoryHealthCheck(MemoryType memoryType,
                             MemoryMetric memoryMetric,
                             IMemoryHealthCheck memoryHealthCheck,
                             HealthCheckTolerance healthCheckTolerance) {
        this.memoryType = memoryType;
        this.memoryMetric = memoryMetric;
        this.memoryHealthCheck = memoryHealthCheck;
        this.healthCheckTolerance = healthCheckTolerance;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getMetricName() {
        return memoryType.getValue();
    }

    @Override
    public Double getMetricValue() {
        switch (memoryType) {
            case FREE_MEMORY:
                return getFreeMemoryValue();
            case UTILIZED_MEMORY:
                return getUtilizedMemoryValue();
            case TOTAL_MEMORY:
                return getTotalMemoryValue();
            default:
                throw new RuntimeException("Invalid Enum Value");
        }
    }

    private Double getFreeMemoryValue() {
        MemoryHealthCheckModel memoryHealthCheckModel = memoryHealthCheck.getMemoryHealthCheck();
        if (memoryMetric == MemoryMetric.BYTES) {
            return Double.valueOf(memoryHealthCheckModel.getFreeMemory());
        }

        return memoryHealthCheckModel.getFreeMemoryPercentage();
    }

    private Double getUtilizedMemoryValue() {
        MemoryHealthCheckModel memoryHealthCheckModel = memoryHealthCheck.getMemoryHealthCheck();

        if (memoryMetric == MemoryMetric.BYTES) {
            return Double.valueOf(memoryHealthCheckModel.getUtilizedMemory());
        }

        return memoryHealthCheckModel.getUtilizedMemoryPercentage();
    }

    private Double getTotalMemoryValue() {
        MemoryHealthCheckModel memoryHealthCheckModel = memoryHealthCheck.getMemoryHealthCheck();

        if (memoryMetric == MemoryMetric.BYTES) {
            return Double.valueOf(memoryHealthCheckModel.getTotalMemory());
        }

        return 100.0;
    }

    @Override
    public HealthCheckTolerance getStatusTolerance() {
        return healthCheckTolerance;
    }

    @Override
    public List<ComponentHealthCheckModel.Value> getComponentValues() {
        return null;
    }
}
