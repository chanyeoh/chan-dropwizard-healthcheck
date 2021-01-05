package com.dropwizard.template.health;

import com.dropwizard.template.health.memory.enums.MemoryType;
import com.dropwizard.template.health.memory.model.MemoryHealthCheckModel;
import com.dropwizard.template.health.model.ComponentInfo;
import com.dropwizard.template.health.model.HealthCheckTolerance;

public class MemoryHealthCheck extends IHealthCheck {

    private MemoryType memoryType;

    static final String VERSION = "1.0";
    static final String DESCRIPTION = "This is a metric that is used to track memory";

    public MemoryHealthCheck(ComponentInfo componentInfo, MemoryType memoryType) {
        super(componentInfo);
        this.memoryType = memoryType;
    }

    @Override
    protected String getVersion() {
        return VERSION;
    }

    @Override
    protected String getDescription() {
        return DESCRIPTION;
    }

    @Override
    protected String getMetricName() {
        return memoryType.getValue();
    }

    @Override
    protected Double getMetricValue() {
        return null;
    }

    @Override
    protected HealthCheckTolerance getStatusTolerance() {
        return null;
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

    /**
     * Gets the essential information for the memory health check in bytes.
     *
     * @return MemoryHealthCheckModel in bytes
     */
    private MemoryHealthCheckModel getMemoryHealthCheck() {
        try {
            Runtime currRuntime = Runtime.getRuntime();
            MemoryHealthCheckModel.MemoryHealthCheckModelBuilder memoryBuilder = MemoryHealthCheckModel.builder();

            long totalMemory = currRuntime.totalMemory();
            memoryBuilder.totalMemory(totalMemory);

            long freeMemory = currRuntime.freeMemory();
            memoryBuilder.freeMemory(freeMemory);

            return memoryBuilder.build();
        } catch (Exception e) {
            // To do store exception output
        }
        return MemoryHealthCheckModel.empty();
    }
}
