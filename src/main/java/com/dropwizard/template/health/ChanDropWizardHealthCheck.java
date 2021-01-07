package com.dropwizard.template.health;

import com.codahale.metrics.health.HealthCheck;
import com.dropwizard.template.health.enums.HealthCheckStatusEnum;
import com.dropwizard.template.health.model.ComponentHealthCheckModel;
import com.dropwizard.template.health.model.ComponentInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;

public class ChanDropWizardHealthCheck extends HealthCheck {
    static final String COLON = ":";

    private final ComponentInfo componentInfo;
    private final IHealthCheckInfo healthCheckInfo;

    public ChanDropWizardHealthCheck(ComponentInfo componentInfo, IHealthCheckInfo healthCheckInfo) {
        this.componentInfo = componentInfo;
        this.healthCheckInfo = healthCheckInfo;
    }

    public String getMetricTitle() {
        String metricName = healthCheckInfo.getMetricName();
        if (StringUtils.isEmpty(metricName)) {
            return componentInfo.getComponentName();
        }

        return componentInfo.getComponentName() +
                COLON +
                metricName;
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
        List<ComponentHealthCheckModel.Value> componentValues =
                healthCheckInfo.getComponentValues();

        return ComponentHealthCheckModel.builder()
                .componentName(componentInfo.getComponentName())
                .metricName(healthCheckInfo.getMetricName())
                .version(healthCheckInfo.getVersion())
                .status(getStatus(componentValues))
                .description(healthCheckInfo.getDescription())
                .componentValue(componentValues)
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
        ImmutableSet<HealthCheckStatusEnum> healthSet = ImmutableSet.of(
                HealthCheckStatusEnum.PASS,
                HealthCheckStatusEnum.WARN
        );

        HealthCheckStatusEnum status = componentHealthCheckModel.getStatus();
        return healthSet.contains(status);
    }

    private String convertComponentHealthCheckToJsonString(ComponentHealthCheckModel componentHealthCheckModel) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        return objectMapper.writeValueAsString(componentHealthCheckModel);
    }

    private void updateResultBuilderWithJsonDetail(String jsonString, ResultBuilder resultBuilder) throws JsonProcessingException {
        HashMap<String, Object> reader = new ObjectMapper().readValue(jsonString, HashMap.class);
        for (String key : reader.keySet()) {
            Object value = reader.get(key);
            resultBuilder.withDetail(key, value);
        }
    }

    private HealthCheckStatusEnum getStatus(List<ComponentHealthCheckModel.Value> componentValues) {
        HealthCheckStatusEnum status = HealthCheckStatusEnum.PASS;
        for (ComponentHealthCheckModel.Value value : componentValues) {
            if (value.getStatus() == HealthCheckStatusEnum.FAIL) {
                return HealthCheckStatusEnum.FAIL;
            }

            if (value.getStatus() == HealthCheckStatusEnum.WARN) {
                status = HealthCheckStatusEnum.WARN;
            }
        }
        return status;
    }
}
