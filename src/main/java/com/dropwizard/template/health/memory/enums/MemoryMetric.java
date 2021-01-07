package com.dropwizard.template.health.memory.enums;

import lombok.Getter;

@Getter
public enum MemoryMetric {
    PERCENTAGE("percentage"),
    BYTES("bytes"),
    PERCENTAGE_BYTES("");

    private final String value;

    MemoryMetric(String value) {
        this.value = value;
    }
}
