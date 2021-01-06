import com.dropwizard.template.health.ChanDropWizardHealthCheck;
import com.dropwizard.template.health.IHealthCheckInfo;
import com.dropwizard.template.health.enums.ToleranceType;
import com.dropwizard.template.health.memory.MemoryHealthCheck;
import com.dropwizard.template.health.memory.RuntimeMemoryHealthCheck;
import com.dropwizard.template.health.memory.enums.MemoryMetric;
import com.dropwizard.template.health.memory.enums.MemoryType;
import com.dropwizard.template.health.model.ComponentInfo;
import com.dropwizard.template.health.model.HealthCheckTolerance;
import com.google.common.collect.ImmutableList;
import io.dropwizard.Application;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

// https://www.baeldung.com/java-dropwizard
// https://www.dropwizard.io/en/latest/manual/core.html
public class TemplateApplication extends Application<TemplateConfiguration> {
    ImmutableList<Object> resourceList = ImmutableList.of(
            new TemplateResource()
    );
    public static void main(String[] args) throws Exception {
        // The first argument can either be server or check, Why the hell isn't it an enum?
        //  check validates if it is a valid argument
        new TemplateApplication().run("server", "introduction-config.yml");
    }

    @Override
    public void run(TemplateConfiguration templateConfiguration, Environment environment) throws Exception {
        // Register Resource classes
        System.out.println(templateConfiguration.getDefaultSize());

        registerResources(environment);
        registerHealthCheck(environment);
    }

    private void registerResources(Environment environment) {
        for (Object resource : resourceList) {
            environment.jersey().register(resource);
        }
    }

    private void registerHealthCheck(Environment environment) {
//        MainApplicationHealthCheck mainHealthCheck = new MainApplicationHealthCheck();
//        environment.healthChecks().register("application", mainHealthCheck);

        ComponentInfo componentInfo = ComponentInfo.builder()
                .componentName("memory")
                .componentId("6fd416e0-8920-410f-9c7b-c479000f7227")
                .componentType("system")
                .build();
        MemoryHealthCheck memoryHealthCheck = new MemoryHealthCheck(MemoryType.UTILIZED_MEMORY,
                MemoryMetric.BYTES, new RuntimeMemoryHealthCheck(),
                HealthCheckTolerance.builder()
                        .passValue(100.0)
                        .warnValue(50.0)
                        .failValue(0.0)
                        .toleranceType(ToleranceType.GREATER_THAN)
                        .build());
        addChanDropWizardHealthCheckToEnvironment(componentInfo, memoryHealthCheck, environment);
    }

    private void addChanDropWizardHealthCheckToEnvironment(ComponentInfo componentInfo, IHealthCheckInfo healthCheckInfo,
                                                           Environment environment) {
        ChanDropWizardHealthCheck chanDropWizardHealthCheck = new ChanDropWizardHealthCheck(componentInfo, healthCheckInfo);
        environment.healthChecks().register(chanDropWizardHealthCheck.getMetricTitle(), chanDropWizardHealthCheck);
    }

    @Override
    public void initialize(Bootstrap<TemplateConfiguration> bootstrap) {
        // Not required
        // ReesourceConfigurationSourceProvider allows the application to find the file of the given resource
        bootstrap.setConfigurationSourceProvider(new ResourceConfigurationSourceProvider());
        super.initialize(bootstrap);
    }
}
