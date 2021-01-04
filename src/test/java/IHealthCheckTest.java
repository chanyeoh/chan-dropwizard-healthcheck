import com.dropwizard.template.health.IHealthCheck;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class IHealthCheckTest {
    static final String INVALID_TOLERANCE_VALUE_ERROR_MESSAGE = "Invalid Tolerance Values";
    public static Object[][] validToleranceDefaultValues() {
        return new Object[][] {
                {0.0, 0.0, 0.0},
                {30.0, 30.0, 30.0},
                {10.0, 30.0, 30.0},
                {10.0, 30.0, 50.0},
        };
    }

    public static Object[][] validToleranceValues() {
        return new Object[][] {
                {0.0, 0.0, 0.0, IHealthCheck.HealthCheckTolerance.ToleranceType.LESS_THAN},
                {0.0, 0.0, 0.0, IHealthCheck.HealthCheckTolerance.ToleranceType.GREATER_THAN},
                {30.0, 30.0, 30.0, IHealthCheck.HealthCheckTolerance.ToleranceType.LESS_THAN},
                {30.0, 30.0, 30.0, IHealthCheck.HealthCheckTolerance.ToleranceType.GREATER_THAN},
                {10.0, 30.0, 30.0, IHealthCheck.HealthCheckTolerance.ToleranceType.LESS_THAN},
                {30.0, 30.0, 10.0, IHealthCheck.HealthCheckTolerance.ToleranceType.GREATER_THAN},
                {10.0, 30.0, 50.0, IHealthCheck.HealthCheckTolerance.ToleranceType.LESS_THAN},
                {50.0, 30.0, 10.0, IHealthCheck.HealthCheckTolerance.ToleranceType.GREATER_THAN},
        };
    }

    public static Object[][] invalidToleranceValues() {
        return new Object[][] {
                {10.0, 30.0, 30.0, IHealthCheck.HealthCheckTolerance.ToleranceType.GREATER_THAN},
                {30.0, 30.0, 10.0, IHealthCheck.HealthCheckTolerance.ToleranceType.LESS_THAN},
                {10.0, 30.0, 50.0, IHealthCheck.HealthCheckTolerance.ToleranceType.GREATER_THAN},
                {50.0, 30.0, 10.0, IHealthCheck.HealthCheckTolerance.ToleranceType.LESS_THAN},
        };
    }

    @ParameterizedTest(name = "{index} => pass={0}, warn={1}, fail={2}")
    @MethodSource("validToleranceDefaultValues")
    public void healthCheckToleranceDefaultTest(Double pass, Double warn, Double fail) {
        IHealthCheck.HealthCheckTolerance.HealthCheckToleranceBuilder healthCheckToleranceBuilder =
                IHealthCheck.HealthCheckTolerance.builder();
        healthCheckToleranceBuilder.passValue(pass);
        healthCheckToleranceBuilder.warnValue(warn);
        healthCheckToleranceBuilder.failValue(fail);

        IHealthCheck.HealthCheckTolerance healthCheckTolerance = healthCheckToleranceBuilder.build();
        assertHealthCheckTolerance(pass, warn, fail, healthCheckTolerance);
    }

    @ParameterizedTest(name = "{index} => pass={0}, warn={1}, fail={2}, toleranceType={3}")
    @MethodSource("validToleranceValues")
    public void healthCheckToleranceTest(Double pass, Double warn, Double fail,
                                         IHealthCheck.HealthCheckTolerance.ToleranceType toleranceType) {
        IHealthCheck.HealthCheckTolerance.HealthCheckToleranceBuilder healthCheckToleranceBuilder =
                IHealthCheck.HealthCheckTolerance.builder();
        healthCheckToleranceBuilder.passValue(pass);
        healthCheckToleranceBuilder.warnValue(warn);
        healthCheckToleranceBuilder.failValue(fail);
        healthCheckToleranceBuilder.toleranceType(toleranceType);

        IHealthCheck.HealthCheckTolerance healthCheckTolerance = healthCheckToleranceBuilder.build();
        assertHealthCheckTolerance(pass, warn, fail, healthCheckTolerance);
    }

    @ParameterizedTest(name = "{index} => pass={0}, warn={1}, fail={2}, toleranceType={3}")
    @MethodSource("invalidToleranceValues")
    public void healthCheckInvalidToleranceTest(Double pass, Double warn, Double fail,
                                         IHealthCheck.HealthCheckTolerance.ToleranceType toleranceType) {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class , () -> {
            IHealthCheck.HealthCheckTolerance.HealthCheckToleranceBuilder healthCheckToleranceBuilder =
                    IHealthCheck.HealthCheckTolerance.builder();
            healthCheckToleranceBuilder.passValue(pass);
            healthCheckToleranceBuilder.warnValue(warn);
            healthCheckToleranceBuilder.failValue(fail);
            healthCheckToleranceBuilder.toleranceType(toleranceType);

            healthCheckToleranceBuilder.build();
        });

        String actualMessage = exception.getMessage();
        Assertions.assertTrue(StringUtils.containsIgnoreCase(actualMessage, INVALID_TOLERANCE_VALUE_ERROR_MESSAGE));
    }



    private void assertHealthCheckTolerance(Double pass, Double warn, Double fail,
                                            IHealthCheck.HealthCheckTolerance healthCheckTolerance) {
        Assertions.assertEquals(healthCheckTolerance.getPassValue(), pass);
        Assertions.assertEquals(healthCheckTolerance.getWarnValue(), warn);
        Assertions.assertEquals(healthCheckTolerance.getFailValue(), fail);
    }
}
