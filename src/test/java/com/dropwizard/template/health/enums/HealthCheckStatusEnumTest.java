package com.dropwizard.template.health.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class HealthCheckStatusEnumTest {
    public static Object[][] testPointForHealthCheck() {
        return new Object[][]{
                {HealthCheckStatusEnum.PASS, "pass"},
                {HealthCheckStatusEnum.WARN, "warn"},
                {HealthCheckStatusEnum.FAIL, "fail"},
        };
    }

    @ParameterizedTest(name = "{index} => statusEnum={0}, expectedString={1}")
    @MethodSource("testPointForHealthCheck")
    public void testOutputForHealthCheckEnum(HealthCheckStatusEnum statusEnum,
                                             String expectedString) {
        Assertions.assertEquals(expectedString, statusEnum.getValue());
    }
}
