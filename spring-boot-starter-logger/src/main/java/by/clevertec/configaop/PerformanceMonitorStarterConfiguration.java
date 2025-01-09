package by.clevertec.configaop;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PerformanceMonitorAutoConfiguration.class)
public class PerformanceMonitorStarterConfiguration {}