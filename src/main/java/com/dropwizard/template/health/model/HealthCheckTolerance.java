package com.dropwizard.template.health.model;

import com.dropwizard.template.health.enums.ToleranceType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HealthCheckTolerance {
    private final Double passValue;
    private final Double warnValue;
    private final Double failValue;
    private ToleranceType toleranceType;

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
