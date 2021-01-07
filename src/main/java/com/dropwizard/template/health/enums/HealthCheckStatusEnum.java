package com.dropwizard.template.health.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @Override
    public String toString() {
        return this.value;
    }
}
