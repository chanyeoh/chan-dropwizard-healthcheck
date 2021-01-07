package com.dropwizard.template.health.memory;

import com.codahale.metrics.json.HealthCheckModule;
import com.dropwizard.template.health.IHealthCheckInfo;
import com.dropwizard.template.health.enums.HealthCheckStatusEnum;
import com.dropwizard.template.health.enums.ToleranceType;
import com.dropwizard.template.health.memory.enums.MemoryMetric;
import com.dropwizard.template.health.memory.enums.MemoryType;
import com.dropwizard.template.health.memory.model.MemoryHealthCheckModel;
import com.dropwizard.template.health.model.ComponentHealthCheckModel;
import com.dropwizard.template.health.model.HealthCheckTolerance;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MemoryHealthCheck implements IHealthCheckInfo {
    private List<MemoryType> memoryTypeList;
    private MemoryMetric memoryMetric;
    private IMemoryHealthCheck memoryHealthCheck;
    private HealthCheckTolerance healthCheckTolerance;

    static final String VERSION = "1.0";
    static final String DESCRIPTION = "This is a metric that is used to track memory";

    public MemoryHealthCheck(List<MemoryType> memoryTypeList,
                             MemoryMetric memoryMetric,
                             IMemoryHealthCheck memoryHealthCheck,
                             HealthCheckTolerance healthCheckTolerance) {
        assertValidMemoryTypeList();
        this.memoryTypeList = memoryTypeList;
        this.memoryMetric = memoryMetric;
        this.memoryHealthCheck = memoryHealthCheck;
        this.healthCheckTolerance = healthCheckTolerance;
    }

    private void assertValidMemoryTypeList() {
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
        if (memoryMetric == MemoryMetric.PERCENTAGE_BYTES) {
            return "";
        }

        if (memoryTypeList.size() == 1) {
            return memoryTypeList.get(0).getValue();
        }
        return "";
    }

    @Override
    public HealthCheckTolerance getStatusTolerance() {
        return healthCheckTolerance;
    }

    @Override
    public List<ComponentHealthCheckModel.Value> getComponentValues() {
        List<MemoryMetric> memoryMetricList = getMemoryMetric();
        MemoryHealthCheckModel memoryHealthCheckModel = this.memoryHealthCheck.getMemoryHealthCheck();

        List<ComponentHealthCheckModel.Value> componentValueList = new ArrayList<>();
        for (MemoryType memoryType : memoryTypeList) {
            for (MemoryMetric memoryMetric : memoryMetricList) {
                Double memoryValue = getMemoryValue(memoryType, memoryMetric,
                        memoryHealthCheckModel);
                ComponentHealthCheckModel.Value componentValue =
                        buildComponentHealthValue(memoryValue, memoryMetric);
                componentValueList.add(componentValue);
            }
        }

        return componentValueList;
    }

    private ComponentHealthCheckModel.Value buildComponentHealthValue(Double memoryValue,
                                                                      MemoryMetric memoryMetric) {
        String message = memoryHealthCheck.getLastErrorMessage().getMessage();
        return ComponentHealthCheckModel.Value.builder()
//                .componentId(componentInfo.getComponentId())
//                .componentType(componentInfo.getComponentType())
                .metricValue(memoryValue)
                .metricUnit(memoryMetric.getValue())
                .status(getStatus(memoryValue))
                .time(new Date())
                .output(message) // Print if there are any errors
                .build();
    }

    private Double getMemoryValue(MemoryType memoryType,
                                  MemoryMetric memoryMetric,
                                  MemoryHealthCheckModel memoryHealthCheckModel) {
        switch (memoryType) {
            case FREE_MEMORY:
                return getFreeMemoryValue(memoryMetric,
                        memoryHealthCheckModel);
            case UTILIZED_MEMORY:
                return getUtilizedMemoryValue(memoryMetric,
                        memoryHealthCheckModel);
            case TOTAL_MEMORY:
                return getTotalMemoryValue(memoryMetric,
                        memoryHealthCheckModel);
            default:
                throw new IllegalArgumentException("Invalid Memory Type");
        }
    }

    private List<MemoryMetric> getMemoryMetric() {
        if (memoryMetric == MemoryMetric.PERCENTAGE_BYTES) {
            return ImmutableList.of(
                    MemoryMetric.PERCENTAGE,
                    MemoryMetric.BYTES
            );
        }
        return ImmutableList.of(
                memoryMetric
        );
    }

    private Double getFreeMemoryValue(MemoryMetric memoryMetric,
                                      MemoryHealthCheckModel memoryHealthCheckModel) {
        assertValidMemoryMetric(memoryMetric);
        if (memoryMetric == MemoryMetric.BYTES) {
            return Double.valueOf(memoryHealthCheckModel.getFreeMemory());
        }

        return memoryHealthCheckModel.getFreeMemoryPercentage();
    }

    private Double getUtilizedMemoryValue(MemoryMetric memoryMetric,
                                          MemoryHealthCheckModel memoryHealthCheckModel) {
        assertValidMemoryMetric(memoryMetric);
        if (memoryMetric == MemoryMetric.BYTES) {
            return Double.valueOf(memoryHealthCheckModel.getUtilizedMemory());
        }

        return memoryHealthCheckModel.getUtilizedMemoryPercentage();
    }

    private Double getTotalMemoryValue(MemoryMetric memoryMetric,
                                       MemoryHealthCheckModel memoryHealthCheckModel) {
        assertValidMemoryMetric(memoryMetric);
        if (memoryMetric == MemoryMetric.BYTES) {
            return Double.valueOf(memoryHealthCheckModel.getTotalMemory());
        }

        return 100.0;
    }

    private void assertValidMemoryMetric(MemoryMetric memoryMetric) {
        if (!isValidMemoryMetric(memoryMetric)) {
            throw new IllegalArgumentException("Invalid Memory Parameter");
        }
    }

    private boolean isValidMemoryMetric(MemoryMetric memoryMetric) {
        return memoryMetric == MemoryMetric.BYTES ||
                memoryMetric == MemoryMetric.PERCENTAGE;
    }

//    private void updateComponentMetric() {
//        MemoryHealthCheckModel memoryHealthCheckModel = getMemoryHealthCheck();
//
//        ComponentHealthCheckModel.Value memoryInfo = ComponentHealthCheckModel.Value.builder()
//                .componentId(componentInfo.getComponentId())
//                .componentType(componentInfo.getComponentType())
//                .metricValue(memoryHealthCheckModel.getUtilizedMemory())
//                .metricUnit("bytes")
//                .status("pass")
//                .time(new Date())
//                .output("") // Print if there are any errors
//                .build();
//        List<ComponentHealthCheckModel.Value> healthCheckValue = ImmutableList.of(
//                memoryInfo
//        );
//
//        componentHealthCheckModel = componentHealthCheckModel.toBuilder()
//                .componentValue(healthCheckValue)
//                .build();
//    }

    private HealthCheckStatusEnum getStatus(Double metric) {
        if (healthCheckTolerance.getToleranceType() == ToleranceType.LESS_THAN) {
            return getLessThanToleranceStatus(metric);
        }
        return getLargerThanToleranceStatus(metric);
    }

    private HealthCheckStatusEnum getLessThanToleranceStatus(Double metric) {
        if (metric <= healthCheckTolerance.getPassValue()) {
            return HealthCheckStatusEnum.PASS;
        }
        if (metric <= healthCheckTolerance.getWarnValue()) {
            return HealthCheckStatusEnum.WARN;
        }
        return HealthCheckStatusEnum.FAIL;
    }

    private HealthCheckStatusEnum getLargerThanToleranceStatus(Double metric) {
        if (metric >= healthCheckTolerance.getPassValue()) {
            return HealthCheckStatusEnum.PASS;
        }
        if (metric >= healthCheckTolerance.getWarnValue()) {
            return HealthCheckStatusEnum.WARN;
        }
        return HealthCheckStatusEnum.FAIL;
    }
}
