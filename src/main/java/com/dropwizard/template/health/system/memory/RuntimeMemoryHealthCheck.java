package com.dropwizard.template.health.system.memory;

import com.dropwizard.template.health.system.model.MemoryHealthCheckModel;

public class RuntimeMemoryHealthCheck implements IMemoryHealthCheck {
    private Exception exceptionMessage;

    public RuntimeMemoryHealthCheck() {
        this.exceptionMessage = null;
    }

    @Override
    public MemoryHealthCheckModel getMemoryHealthCheck() {
        this.exceptionMessage = null;

        try {
            Runtime currRuntime = Runtime.getRuntime();
            MemoryHealthCheckModel.MemoryHealthCheckModelBuilder memoryBuilder = MemoryHealthCheckModel.builder();

            long totalMemory = currRuntime.totalMemory();
            memoryBuilder.totalMemory(totalMemory);

            long freeMemory = currRuntime.freeMemory();
            memoryBuilder.freeMemory(freeMemory);

            return memoryBuilder.build();
        } catch (Exception e) {
            this.exceptionMessage = e;
        }
        return MemoryHealthCheckModel.empty();
    }

    @Override
    public Exception getLastErrorMessage() {
        return this.exceptionMessage;
    }
}
