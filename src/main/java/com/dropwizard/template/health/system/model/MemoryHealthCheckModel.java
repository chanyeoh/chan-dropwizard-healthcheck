package com.dropwizard.template.health.system.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemoryHealthCheckModel {
    long totalMemory;
    long freeMemory;

    public static MemoryHealthCheckModel empty() {
        return new MemoryHealthCheckModel(-1, -1);
    }

    public Long getUtilizedMemory() {
        assertValidMemory();
        return getTotalMemory() - getFreeMemory();
    }

    public Double getFreeMemoryPercentage() {
        assertValidMemory();
        return Double.valueOf(getFreeMemory()) / Double.valueOf(getTotalMemory()) * 100;
    }

    public Double getUtilizedMemoryPercentage() {
        assertValidMemory();
        return Double.valueOf(getUtilizedMemory()) / Double.valueOf(getTotalMemory()) * 100;
    }

    private void assertValidMemory() {
        if (!isValidMemory()) {
            throw new IllegalArgumentException("Total Memory must be > 0 and Free Memory must be >= 0");
        }
    }

    private boolean isValidMemory() {
        return totalMemory > 0 &&
                freeMemory >= 0;
    }
}
