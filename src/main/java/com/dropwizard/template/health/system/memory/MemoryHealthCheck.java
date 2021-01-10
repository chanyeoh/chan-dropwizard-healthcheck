package com.dropwizard.template.health.system.memory;

import com.dropwizard.template.health.IHealthCheckInfo;
import com.dropwizard.template.health.enums.HealthCheckStatusEnum;
import com.dropwizard.template.health.system.MetricTolerance;
import com.dropwizard.template.health.system.enums.Metric;
import com.dropwizard.template.health.system.enums.MemoryType;
import com.dropwizard.template.health.system.model.MemoryHealthCheckModel;
import com.dropwizard.template.health.model.ComponentHealthCheckModel;
import com.dropwizard.template.health.model.ComponentInfo;
import com.dropwizard.template.health.model.HealthCheckTolerance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MemoryHealthCheck implements IHealthCheckInfo {
    private final List<MemoryType> memoryTypeList;
    private final MetricTolerance metricTolerance;
    private final ComponentInfo componentInfo;
    private final IMemoryHealthCheck memoryHealthCheck;

    static final String VERSION = "1.0";
    static final String DESCRIPTION = "This is a metric that is used to track memory";

    public MemoryHealthCheck(List<MemoryType> memoryTypeList,
                             MetricTolerance metricTolerance,
                             ComponentInfo componentInfo,
                             IMemoryHealthCheck memoryHealthCheck) {
        assertValidMemoryTypeList(memoryTypeList);
        this.memoryTypeList = memoryTypeList;
        this.metricTolerance = metricTolerance;
        this.componentInfo = componentInfo;
        this.memoryHealthCheck = memoryHealthCheck;
    }

    private void assertValidMemoryTypeList(List<MemoryType> memoryTypeList) {
        if (memoryTypeList.size() == 0) {
            throw new IllegalArgumentException("We must process a memory type");
        }
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
        if (memoryTypeList.size() == 1) {
            return memoryTypeList.get(0).getValue();
        }

        return "";
    }

    @Override
    public List<ComponentHealthCheckModel.Value> getComponentValues() {
        List<Metric> metricList = metricTolerance.getMeasureableMetric();
        MemoryHealthCheckModel memoryHealthCheckModel = this.memoryHealthCheck.getMemoryHealthCheck();

        List<ComponentHealthCheckModel.Value> componentValueList = new ArrayList<>();
        for (MemoryType memoryType : memoryTypeList) {
            for (Metric metric : metricList) {
                Double memoryValue = getMemoryValue(memoryType, metric,
                        memoryHealthCheckModel);
                ComponentHealthCheckModel.Value componentValue =
                        buildComponentHealthValue(memoryValue, metric);
                componentValueList.add(componentValue);
            }
        }

        return componentValueList;
    }

    private ComponentHealthCheckModel.Value buildComponentHealthValue(Double memoryValue,
                                                                      Metric metric) {
        String message = memoryHealthCheck.getLastErrorMessage().getMessage();
        return ComponentHealthCheckModel.Value.builder()
                .componentId(componentInfo.getComponentId())
                .componentType(componentInfo.getComponentType())
                .metricValue(memoryValue)
                .metricUnit(metric.getValue())
                .status(getStatus(metric, memoryValue))
                .time(new Date())
                .output(message) // Print if there are any errors
                .build();
    }

    private Double getMemoryValue(MemoryType memoryType,
                                  Metric metric,
                                  MemoryHealthCheckModel memoryHealthCheckModel) {
        switch (memoryType) {
            case FREE_MEMORY:
                return getFreeMemoryValue(metric,
                        memoryHealthCheckModel);
            case UTILIZED_MEMORY:
                return getUtilizedMemoryValue(metric,
                        memoryHealthCheckModel);
            case TOTAL_MEMORY:
                return getTotalMemoryValue(metric,
                        memoryHealthCheckModel);
            default:
                throw new IllegalArgumentException("Invalid Memory Type");
        }
    }

    private Double getFreeMemoryValue(Metric metric,
                                      MemoryHealthCheckModel memoryHealthCheckModel) {
        assertValidMemoryMetric(metric);
        if (metric == Metric.BYTES) {
            return Double.valueOf(memoryHealthCheckModel.getFreeMemory());
        }

        return memoryHealthCheckModel.getFreeMemoryPercentage();
    }

    private Double getUtilizedMemoryValue(Metric metric,
                                          MemoryHealthCheckModel memoryHealthCheckModel) {
        assertValidMemoryMetric(metric);
        if (metric == Metric.BYTES) {
            return Double.valueOf(memoryHealthCheckModel.getUtilizedMemory());
        }

        return memoryHealthCheckModel.getUtilizedMemoryPercentage();
    }

    private Double getTotalMemoryValue(Metric metric,
                                       MemoryHealthCheckModel memoryHealthCheckModel) {
        assertValidMemoryMetric(metric);
        if (metric == Metric.BYTES) {
            return Double.valueOf(memoryHealthCheckModel.getTotalMemory());
        }

        return 100.0;
    }

    private void assertValidMemoryMetric(Metric metric) {
        if (!isValidMemoryMetric(metric)) {
            throw new IllegalArgumentException("Invalid Memory Parameter");
        }
    }

    private boolean isValidMemoryMetric(Metric metric) {
        return metric == Metric.BYTES ||
                metric == Metric.PERCENTAGE;
    }

    private HealthCheckStatusEnum getStatus(Metric metric, Double value) {
        HealthCheckTolerance healthCheckTolerance = getHealthCheckTolerance(metric);
        return healthCheckTolerance.getHealthCheckStatus(value);
    }

    private HealthCheckTolerance getHealthCheckTolerance(Metric metric) {
        return metricTolerance.getMetricTolerance(metric);
    }
}
