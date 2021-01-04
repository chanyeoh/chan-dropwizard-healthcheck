package com.dropwizard.template.health;

import com.dropwizard.template.health.model.ComponentInfo;
import lombok.Builder;
import lombok.Getter;

public class MemoryHealthCheck extends IHealthCheck {
    @Getter
    public enum MemoryType {
        FREE_MEMORY("freeMemory"),
        TOTAL_MEMORY("totalMemory"),
        UTILIZED_MEMORY("utilizedMemory");

        private String value;

        MemoryType(String value) {
            this.value = value;
        }
    }

    @Getter
    @Builder
    private static class MemoryHealthCheckModel {
        long totalMemory;
        long freeMemory;

        public static MemoryHealthCheckModel empty() {
            return new MemoryHealthCheckModel(-1, -1);
        }

        public long getUtilizedMemory() {
            if (!isValidMemory()) {
                return -1;
            }

            return totalMemory - freeMemory;
        }

        private boolean isValidMemory() {
            return totalMemory >= 0 &&
                    freeMemory >= 0;
        }
    }

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

//    protected Result getHealthCheckResult() throws JsonProcessingException {
//        updateComponentMetric();
//
//        ResultBuilder resultBuilder = Result.builder();
//        resultBuilder.healthy();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        String value = objectMapper.writeValueAsString(componentHealthCheckModel);
//        System.out.println(value);
//
//        HashMap<String, Object> reader = new ObjectMapper().readValue(value, HashMap.class);
//
//        for (String k : reader.keySet()) {
//            resultBuilder.withDetail(k, reader.get(k));
//        }
//        return resultBuilder.build();
//    }

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
