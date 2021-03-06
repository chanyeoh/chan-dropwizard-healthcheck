package com.dropwizard.template.health.system.enums;

import lombok.Getter;

@Getter
public enum Metric {
    PERCENTAGE("percentage"),
    BYTES("bytes");

    private final String value;

    Metric(String value) {
        this.value = value;
    }
}
