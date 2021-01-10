package com.dropwizard.template.health.system.enums;

import lombok.Getter;

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
