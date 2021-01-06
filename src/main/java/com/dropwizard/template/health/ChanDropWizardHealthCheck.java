package com.dropwizard.template.health;

import com.codahale.metrics.health.HealthCheck;
import com.dropwizard.template.health.enums.HealthCheckStatusEnum;
import com.dropwizard.template.health.enums.ToleranceType;
import com.dropwizard.template.health.model.ComponentHealthCheckModel;
import com.dropwizard.template.health.model.ComponentInfo;
import com.dropwizard.template.health.model.HealthCheckTolerance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableSet;

import java.util.HashMap;

public class ChanDropWizardHealthCheck extends HealthCheck {
    static final String COLON = ":";

    private final ComponentInfo componentInfo;
    private final IHealthCheckInfo healthCheckInfo;

    public ChanDropWizardHealthCheck(ComponentInfo componentInfo, IHealthCheckInfo healthCheckInfo) {
        this.componentInfo = componentInfo;
        this.healthCheckInfo = healthCheckInfo;
    }

    public String getMetricTitle() {
        return componentInfo.getComponentName() +
                COLON +
                healthCheckInfo.getMetricName();
    }

    public Result getHealthCheckResult() throws JsonProcessingException {
        ComponentHealthCheckModel componentHealthCheckModel = getLatestHealthCheckResults();
        return convertComponentHealthCheckModelToResult(componentHealthCheckModel);
    }

    @Override
    protected Result check() throws Exception {
        return getHealthCheckResult();
    }

    private ComponentHealthCheckModel getLatestHealthCheckResults() {
        return ComponentHealthCheckModel.builder()
                .componentName(componentInfo.getComponentName())
                .metricName(healthCheckInfo.getMetricName())
                .version(healthCheckInfo.getVersion())
                .status(getStatus().getValue())
                .description(healthCheckInfo.getDescription())
                .componentValue(healthCheckInfo.getComponentValues())
                .build();
    }

    private Result convertComponentHealthCheckModelToResult(ComponentHealthCheckModel componentHealthCheckModel) throws JsonProcessingException {
        ResultBuilder resultBuilder = Result.builder();
        if (isHealthy(componentHealthCheckModel)) {
            resultBuilder.healthy();
        } else {
            resultBuilder.unhealthy();
        }

        String jsonString = convertComponentHealthCheckToJsonString(componentHealthCheckModel);
        updateResultBuilderWithJsonDetail(jsonString, resultBuilder);
        return resultBuilder.build();
    }

    private boolean isHealthy(ComponentHealthCheckModel componentHealthCheckModel) {
        ImmutableSet<String> healthSet = ImmutableSet.of(
                HealthCheckStatusEnum.PASS.getValue(),
                HealthCheckStatusEnum.WARN.getValue()
        );

        String status = componentHealthCheckModel.getStatus();
        return healthSet.contains(status);
    }

    private String convertComponentHealthCheckToJsonString(ComponentHealthCheckModel componentHealthCheckModel) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(componentHealthCheckModel);
    }

    private void updateResultBuilderWithJsonDetail(String jsonString, ResultBuilder resultBuilder) throws JsonProcessingException {
        HashMap<String, Object> reader = new ObjectMapper().readValue(jsonString, HashMap.class);
        for (String key : reader.keySet()) {
            Object value = reader.get(key);
            resultBuilder.withDetail(key, value);
        }
    }

    private HealthCheckStatusEnum getStatus() {
        HealthCheckTolerance healthCheckTolerance = healthCheckInfo.getStatusTolerance();
        if (healthCheckTolerance.getToleranceType() == ToleranceType.LESS_THAN) {
            return getLessThanToleranceStatus(healthCheckTolerance);
        }
        return getLargerThanToleranceStatus(healthCheckTolerance);
    }

    private HealthCheckStatusEnum getLessThanToleranceStatus(HealthCheckTolerance healthCheckTolerance) {
        if (healthCheckInfo.getMetricValue() <= healthCheckTolerance.getPassValue()) {
            return HealthCheckStatusEnum.PASS;
        }
        if (healthCheckInfo.getMetricValue() <= healthCheckTolerance.getWarnValue()) {
            return HealthCheckStatusEnum.WARN;
        }
        return HealthCheckStatusEnum.FAIL;
    }

    private HealthCheckStatusEnum getLargerThanToleranceStatus(HealthCheckTolerance healthCheckTolerance) {
        if (healthCheckInfo.getMetricValue() >= healthCheckTolerance.getPassValue()) {
            return HealthCheckStatusEnum.PASS;
        }
        if (healthCheckInfo.getMetricValue() >= healthCheckTolerance.getWarnValue()) {
            return HealthCheckStatusEnum.WARN;
        }
        return HealthCheckStatusEnum.FAIL;
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
}
