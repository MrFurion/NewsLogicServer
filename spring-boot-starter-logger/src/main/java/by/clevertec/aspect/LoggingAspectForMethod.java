package by.clevertec.aspect;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
@ConditionalOnProperty(prefix = "performance.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class LoggingAspectForMethod {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspectForMethod.class);

    private final DefaultPropertiesLoggingAspect properties;

    @Around("@annotation(by.clevertec.annotation.MonitorPerformance)")
    public Object logAndMonitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        String methodName = joinPoint.getSignature().getName();

        logger.info("Incoming request: [{}] {}{} for method: {}", method, uri, queryString, methodName);

        long start = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            logger.error("Error during request processing in method {}: {}", methodName, ex.getMessage(), ex);
            throw ex;
        }
        long executionTime = System.currentTimeMillis() - start;

        if (executionTime >= properties.getMinExecutionTime()) {
            logger.info("Method {}.{} took {} ms", joinPoint.getSignature().getDeclaringTypeName(), methodName, executionTime);
        }

        if (result != null) {
            if (result instanceof ResponseEntity) {
                ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
                logger.info("Outgoing response: Status Code: {}, Headers: {}", responseEntity.getStatusCode(), responseEntity.getHeaders());
            } else {
                logger.info("Outgoing response: non-ResponseEntity result.");
            }
        } else {
            logger.info("Outgoing response: no content");
        }

        return result;
    }
}

