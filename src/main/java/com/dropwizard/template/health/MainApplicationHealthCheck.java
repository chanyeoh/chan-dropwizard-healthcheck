package com.dropwizard.template.health;

import com.codahale.metrics.health.HealthCheck;

public class MainApplicationHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.builder()
                .build();
    }
}
