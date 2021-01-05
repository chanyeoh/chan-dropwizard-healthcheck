import com.dropwizard.template.health.model.ComponentInfo;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;

public class ComponentInfoTest {
    static final Integer COMPONENT_NAME_LENGTH = 15;
    static final Integer COMPONENT_TYPE_LENGTH = 15;

    static final String VALID_COMPONENT_ID = UUID.randomUUID().toString();
    static final String VALID_COMPONENT_NAME = RandomStringUtils.randomAlphanumeric(COMPONENT_NAME_LENGTH);
    static final String VALID_COMPONENT_TYPE = RandomStringUtils.randomAlphanumeric(COMPONENT_TYPE_LENGTH);

    static final String INVALID_COMPONENT_NAME_ERROR_MESSAGE = "The string must not contain colon";

    public static String[][] testPointForValidComponentInfoBuilder() {
        return new String[][]{
                {VALID_COMPONENT_ID, VALID_COMPONENT_NAME, VALID_COMPONENT_TYPE},
                {"", VALID_COMPONENT_NAME, VALID_COMPONENT_TYPE},
                {VALID_COMPONENT_ID, "", VALID_COMPONENT_TYPE},
                {VALID_COMPONENT_ID, VALID_COMPONENT_NAME, ""},
                {"", "", VALID_COMPONENT_TYPE},
                {"", VALID_COMPONENT_NAME, ""},
                {VALID_COMPONENT_ID, "", ""},
        };
    }

    @ParameterizedTest(name = "{index} => componentId={0}, componentName={1}, componentType={2}")
    @MethodSource("testPointForValidComponentInfoBuilder")
    public void validComponentInfoBuilderTest(String componentId, String componentName, String componentType) {
        ComponentInfo.ComponentInfoBuilder componentInfoBuilder = ComponentInfo.builder();
        componentInfoBuilder.componentId(componentId);
        componentInfoBuilder.componentName(componentName);
        componentInfoBuilder.componentType(componentType);

        ComponentInfo componentInfo = componentInfoBuilder.build();
        assertComponentInfo(componentInfo, componentId, componentName, componentType);
    }

    public static String[][] testPointForInvalidComponentInfoBuilder() {
        return new String[][]{
                {VALID_COMPONENT_ID, VALID_COMPONENT_NAME + ":" + VALID_COMPONENT_NAME, VALID_COMPONENT_TYPE},
                {VALID_COMPONENT_ID, VALID_COMPONENT_NAME + ":", VALID_COMPONENT_TYPE},
                {VALID_COMPONENT_ID, ":" + VALID_COMPONENT_NAME, VALID_COMPONENT_TYPE},
                {VALID_COMPONENT_ID, ":", VALID_COMPONENT_TYPE},
        };
    }

    @ParameterizedTest(name = "{index} => componentId={0}, componentName={1}, componentType={2}")
    @MethodSource("testPointForInvalidComponentInfoBuilder")
    public void invalidComponentInfoBuilderTest(String componentId, String componentName, String componentType) {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ComponentInfo.ComponentInfoBuilder componentInfoBuilder = ComponentInfo.builder();
            componentInfoBuilder.componentId(componentId);
            componentInfoBuilder.componentName(componentName);
            componentInfoBuilder.componentType(componentType);
            componentInfoBuilder.build();
        });

        String actualMessage = exception.getMessage();
        Assertions.assertTrue(StringUtils.containsIgnoreCase(actualMessage, INVALID_COMPONENT_NAME_ERROR_MESSAGE));
    }

    private void assertComponentInfo(ComponentInfo componentInfo, String expectedComponentId,
                                     String expectedComponentName, String expectedComponentType) {
        Assertions.assertEquals(expectedComponentId, componentInfo.getComponentId());
        Assertions.assertEquals(expectedComponentName, componentInfo.getComponentName());
        Assertions.assertEquals(expectedComponentType, componentInfo.getComponentType());
    }
}
