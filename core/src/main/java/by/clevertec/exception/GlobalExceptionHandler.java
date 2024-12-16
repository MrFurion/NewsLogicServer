package by.clevertec.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String DESCRIPTION = "description";

    @ExceptionHandler(NewsNotFoundException.class)
    private ProblemDetail handleSensorNotFoundException(NewsNotFoundException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "News not found");
        return errorDetail;
    }
}
