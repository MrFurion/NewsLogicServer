package by.clevertec.configaop;



import by.clevertec.aspect.DefaultPropertiesLoggingAspect;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableConfigurationProperties(DefaultPropertiesLoggingAspect.class)
@EnableAspectJAutoProxy
@ComponentScan("by.clevertec")
public class PerformanceMonitorAutoConfiguration {
}
