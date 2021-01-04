import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class TemplateConfiguration extends Configuration {
    @NotNull private final int defaultSize;

    @JsonCreator
    public TemplateConfiguration(@JsonProperty("defaultSize") int defaultSize) {
        this.defaultSize = defaultSize;
    }

}
