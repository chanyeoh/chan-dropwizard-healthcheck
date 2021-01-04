package com.dropwizard.template.health.model;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Builder
public class ComponentInfo {
    static final String COLON = ":";

    private final String componentId;
    private final String componentName;
    private final String componentType;

    public static class ComponentInfoBuilder {
        public ComponentInfoBuilder componentName(String componentName) {
            if (StringUtils.contains(componentName, COLON)) {
                throw new IllegalArgumentException("The string must not contain colon");
            }
            this.componentName = componentName;
            return this;
        }
    }
}
