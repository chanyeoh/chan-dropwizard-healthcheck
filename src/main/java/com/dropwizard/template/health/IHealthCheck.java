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

public abstract class IHealthCheck extends HealthCheck {
    static final String COLON = ":";
    private final ComponentInfo componentInfo;

    public IHealthCheck(ComponentInfo componentInfo) {
        this.componentInfo = componentInfo;
    }

    protected abstract String getVersion();

    protected abstract String getDescription();

    protected abstract String getMetricName();

    protected abstract Double getMetricValue();

    protected abstract HealthCheckTolerance getStatusTolerance();

    public String getMetricTitle() {
        return componentInfo.getComponentName() +
                COLON +
                getMetricName();
    }

    @Override
    protected Result check() throws Exception {
        return getHealthCheckResult();
    }

    public Result getHealthCheckResult() throws JsonProcessingException {
        ComponentHealthCheckModel componentHealthCheckModel = getLatestHealthCheckResults();
        return convertComponentHealthCheckModelToResult(componentHealthCheckModel);
    }

    protected ComponentHealthCheckModel getLatestHealthCheckResults() {
        return ComponentHealthCheckModel.builder()
                .componentName(componentInfo.getComponentName())
                .metricName(getMetricName())
                .version(getVersion())
                .description(getDescription())
                .build();
    }

    protected Result convertComponentHealthCheckModelToResult(ComponentHealthCheckModel componentHealthCheckModel) throws JsonProcessingException {
        ResultBuilder resultBuilder = Result.builder();
        if (isHealthy()) {
            resultBuilder.healthy();
        } else {
            resultBuilder.unhealthy();
        }

        String jsonString = convertComponentHealthCheckToJsonString(componentHealthCheckModel);
        updateResultBuilderWithJsonDetail(jsonString, resultBuilder);
        return resultBuilder.build();
    }

    protected boolean isHealthy() {
        ImmutableSet<HealthCheckStatusEnum> healthSet = ImmutableSet.of(
                HealthCheckStatusEnum.PASS,
                HealthCheckStatusEnum.WARN
        );

        HealthCheckStatusEnum status = getStatus();
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

    protected HealthCheckStatusEnum getStatus() {
        HealthCheckTolerance healthCheckTolerance = getStatusTolerance();
        if (healthCheckTolerance.getToleranceType() == ToleranceType.LESS_THAN) {
            return getLessThanToleranceStatus(healthCheckTolerance);
        }
        return getLargerThanToleranceStatus(healthCheckTolerance);
    }

    private HealthCheckStatusEnum getLessThanToleranceStatus(HealthCheckTolerance healthCheckTolerance) {
        if (getMetricValue() <= healthCheckTolerance.getPassValue()) {
            return HealthCheckStatusEnum.PASS;
        }
        if (getMetricValue() <= healthCheckTolerance.getPassValue()) {
            return HealthCheckStatusEnum.WARN;
        }
        return HealthCheckStatusEnum.FAIL;
    }

    private HealthCheckStatusEnum getLargerThanToleranceStatus(HealthCheckTolerance healthCheckTolerance) {
        if (getMetricValue() >= healthCheckTolerance.getPassValue()) {
            return HealthCheckStatusEnum.PASS;
        }
        if (getMetricValue() >= healthCheckTolerance.getPassValue()) {
            return HealthCheckStatusEnum.WARN;
        }
        return HealthCheckStatusEnum.FAIL;
    }
}
