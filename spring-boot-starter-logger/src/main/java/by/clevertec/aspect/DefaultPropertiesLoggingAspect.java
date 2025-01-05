package by.clevertec.aspect;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "performance.monitor")
@Getter
@Setter
public class DefaultPropertiesLoggingAspect {

    private boolean enabled = true;

    private long minExecutionTime = 100;
}
