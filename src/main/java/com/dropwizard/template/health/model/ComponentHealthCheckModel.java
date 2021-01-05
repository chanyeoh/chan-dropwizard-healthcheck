package com.dropwizard.template.health.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

import java.net.URI;
import java.time.Clock;
import java.util.Date;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComponentHealthCheckModel {
    private final String componentName;
    private final String metricName;
    private final String status;
    private final String version;
    private final String description;
    private final List<Value> componentValue;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Value {
        private final String componentId;
        private final String componentType;
        private final Object metricValue; // Could be any metric
        private final String metricUnit;
        private final String status;
        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone="UTC")
        private final Date time;
        private final String output; // Can be used to show error messages
        private final URI link; // Can be used to view the link
    }
}
