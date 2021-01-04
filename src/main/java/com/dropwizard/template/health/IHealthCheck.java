package com.dropwizard.template.health;

import com.codahale.metrics.health.HealthCheck;
import com.dropwizard.template.health.model.ComponentHealthCheckModel;
import com.dropwizard.template.health.model.ComponentInfo;
import lombok.Builder;
import lombok.Getter;

public abstract class IHealthCheck extends HealthCheck {
    @Getter
    public enum Status {
        PASS("pass"),
        WARN("warn"),
        FAIL("fail");

        private final String value;
        Status(String value) {
            this.value = value;
        }
    }

    @Getter
    @Builder
    public static class HealthCheckTolerance {
        public enum ToleranceType {
            LESS_THAN,
            GREATER_THAN;
        }

        private final Double passValue;
        private final Double warnValue;
        private final Double failValue;
        private final ToleranceType toleranceType;

        public static HealthCheckToleranceBuilder builder() {
            return new HealthCheckToleranceBuilder() {
                @Override
                public HealthCheckTolerance build() {
                    prebuild();
                    return super.build();
                }
            };
        }

        public static class HealthCheckToleranceBuilder {
            private ToleranceType toleranceType = ToleranceType.LESS_THAN;

            protected void prebuild() {
                if (!isValidTolerance()) {
                    throw new IllegalArgumentException("Invalid Tolerance Values");
                }
            }

            private boolean isValidTolerance() {
                if (toleranceType == ToleranceType.LESS_THAN) {
                    return isValidToleranceLessThan();
                }
                return isValidToleranceLargerThan();
            }

            private boolean isValidToleranceLessThan() {
                return passValue <= warnValue &&
                        warnValue <= failValue;
            }

            private boolean isValidToleranceLargerThan() {
                return passValue >= warnValue &&
                        warnValue >= failValue;
            }
        }
    }

    private ComponentInfo componentInfo;

    static final String COLON = ":";

    protected abstract String getVersion();
    protected abstract String getDescription();
    protected abstract String getMetricName();
    protected abstract Double getMetricValue();
    protected abstract HealthCheckTolerance getStatusTolerance();

    public IHealthCheck(ComponentInfo componentInfo) {
        this.componentInfo = componentInfo;
    }

    public String getMetricTitle() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(componentInfo.getComponentName());
        stringBuilder.append(COLON);
        stringBuilder.append(getMetricName());

        return stringBuilder.toString();
    }

    @Override
    protected Result check() throws Exception {
        return getHealthCheckResult();
    }

    protected ComponentHealthCheckModel getLatestHealthCheckResults() {
        return ComponentHealthCheckModel.builder()
                .componentName(componentInfo.getComponentName())
                .metricName(getMetricName())
                .version(getVersion())
                .description(getDescription())
                .build();
    }

    protected Result convertComponentHealthCheckModelToResult(ComponentHealthCheckModel componentHealthCheckModel) {

    }

    protected boolean isHealthy() {
        Status status = getStatus();
        return status == Status.PASS 
    }

    protected Result getHealthCheckResult() {
        return null;
    }

    protected Status getStatus() {
        HealthCheckTolerance healthCheckTolerance = getStatusTolerance();
        if (healthCheckTolerance.getToleranceType() == HealthCheckTolerance.ToleranceType.LESS_THAN) {
            return getLessThanToleranceStatus(healthCheckTolerance);
        }
        return getLargerThanToleranceStatus(healthCheckTolerance);
    }

    private Status getLessThanToleranceStatus(HealthCheckTolerance healthCheckTolerance) {
        if (getMetricValue() <= healthCheckTolerance.getPassValue()) {
            return Status.PASS;
        }
        if (getMetricValue() <= healthCheckTolerance.getPassValue()) {
            return Status.WARN;
        }
        return Status.FAIL;
    }

    private Status getLargerThanToleranceStatus(HealthCheckTolerance healthCheckTolerance) {
        if (getMetricValue() >= healthCheckTolerance.getPassValue()) {
            return Status.PASS;
        }
        if (getMetricValue() >= healthCheckTolerance.getPassValue()) {
            return Status.WARN;
        }
        return Status.FAIL;
    }
}
