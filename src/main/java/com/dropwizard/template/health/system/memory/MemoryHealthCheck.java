package com.dropwizard.template.health.system.memory;

import com.dropwizard.template.health.IHealthCheckInfo;
import com.dropwizard.template.health.enums.HealthCheckStatusEnum;
import com.dropwizard.template.health.system.MetricTolerance;
import com.dropwizard.template.health.system.enums.Metric;
import com.dropwizard.template.health.system.enums.MemoryType;
import com.dropwizard.template.health.system.model.MemoryHealthCheckModel;
import com.dropwizard.template.health.model.ComponentHealthCheckModel;
import com.dropwizard.template.health.model.ComponentInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MemoryHealthCheck implements IHealthCheckInfo {
    private final List<MemoryType> memoryTypeList;
    private final List<MetricTolerance> metricToleranceList;
    private final ComponentInfo componentInfo;
    private final IMemoryHealthCheck memoryHealthCheck;

    static final String VERSION = "1.0";
    static final String DESCRIPTION = "This is a metric that is used to track memory";

    public MemoryHealthCheck(List<MemoryType> memoryTypeList,
                             List<MetricTolerance> metricToleranceList,
                             ComponentInfo componentInfo,
                             IMemoryHealthCheck memoryHealthCheck) {
        assertValidMemoryTypeList(memoryTypeList);
        this.memoryTypeList = memoryTypeList;
        this.metricToleranceList = metricToleranceList;
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
        List<ComponentHealthCheckModel.Value> componentValueList = new ArrayList<>();
        MemoryHealthCheckModel memoryHealthCheckModel = memoryHealthCheck.getMemoryHealthCheck();

        for (MemoryType memoryType : memoryTypeList) {
            for (MetricTolerance metricTolerance : metricToleranceList) {
                Double memoryValue = getMemoryValue(memoryType, metricTolerance,
                        memoryHealthCheckModel);
                ComponentHealthCheckModel.Value componentValue =
                        buildComponentHealthValue(memoryValue, metricTolerance);
                componentValueList.add(componentValue);
            }
        }

        return componentValueList;
    }

    private Double getMemoryValue(MemoryType memoryType,
                                  MetricTolerance metricTolerance,
                                  MemoryHealthCheckModel memoryHealthCheckModel) {
        switch (memoryType) {
            case FREE_MEMORY:
                return getFreeMemoryValue(metricTolerance, memoryHealthCheckModel);
            case UTILIZED_MEMORY:
                return getUtilizedMemoryValue(metricTolerance, memoryHealthCheckModel);
            case TOTAL_MEMORY:
                return getTotalMemoryValue(metricTolerance, memoryHealthCheckModel);
            default:
                throw new IllegalArgumentException("Invalid Memory Type");
        }
    }


    private ComponentHealthCheckModel.Value buildComponentHealthValue(Double memoryValue,
                                                                      MetricTolerance metricTolerance) {
        Metric metric = metricTolerance.getMetric();
        HealthCheckStatusEnum healthCheckStatus= metricTolerance.getMetricHealthCheck(memoryValue);
        String message = memoryHealthCheck.getLastErrorMessage();

        return ComponentHealthCheckModel.Value.builder()
                .componentId(componentInfo.getComponentId())
                .componentType(componentInfo.getComponentType())
                .metricValue(memoryValue)
                .metricUnit(metric.getValue())
                .status(healthCheckStatus)
                .time(new Date())
                .output(message) // Print if there are any errors
                .build();
    }

    private Double getFreeMemoryValue(MetricTolerance metricTolerance,
                                      MemoryHealthCheckModel memoryHealthCheckModel) {
        if (metricTolerance.getMetric() == Metric.BYTES) {
            return Double.valueOf(memoryHealthCheckModel.getFreeMemory());
        }

        return memoryHealthCheckModel.getFreeMemoryPercentage();
    }

    private Double getUtilizedMemoryValue(MetricTolerance metricTolerance,
                                          MemoryHealthCheckModel memoryHealthCheckModel) {
        if (metricTolerance.getMetric() == Metric.BYTES) {
            return Double.valueOf(memoryHealthCheckModel.getUtilizedMemory());
        }

        return memoryHealthCheckModel.getUtilizedMemoryPercentage();
    }

    private Double getTotalMemoryValue(MetricTolerance metricTolerance,
                                       MemoryHealthCheckModel memoryHealthCheckModel) {
        if (metricTolerance.getMetric() == Metric.BYTES) {
            return Double.valueOf(memoryHealthCheckModel.getTotalMemory());
        }

        return 100.0;
    }
}
