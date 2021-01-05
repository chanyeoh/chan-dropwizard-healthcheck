package com.dropwizard.template.health.enums;

import lombok.Getter;

@Getter
public enum HealthCheckStatusEnum {
    PASS("pass"),
    WARN("warn"),
    FAIL("fail");

    private final String value;

    HealthCheckStatusEnum(String value) {
        this.value = value;
    }
}
