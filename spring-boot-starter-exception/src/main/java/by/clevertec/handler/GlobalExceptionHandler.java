package by.clevertec.handler;

import by.clevertec.exceptions.CacheException;
import by.clevertec.exceptions.CommentNotFoundException;
import by.clevertec.exceptions.NewsNotFoundException;
import by.clevertec.exceptions.UserNameNotFoundException;
import by.clevertec.model.ErrorDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static by.clevertec.util.ExtracterErrorMessage.extractErrorMessage;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DESCRIPTION = "description";

    @ExceptionHandler(NewsNotFoundException.class)
    private ProblemDetail handleNewsNotFoundException(NewsNotFoundException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "News not found");
        return errorDetail;
    }

    @ExceptionHandler(CommentNotFoundException.class)
    private ProblemDetail handleCommentNotFoundException(CommentNotFoundException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "Comment not found");
        return errorDetail;
    }

    @ExceptionHandler(UserNameNotFoundException.class)
    private ProblemDetail handleUserNameNotFoundException(UserNameNotFoundException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "User name not found");
        return errorDetail;
    }


    @ExceptionHandler(CacheException.class)
    private ProblemDetail handleCacheException(CacheException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "Please enter a policy of LRU, LFU");
        return errorDetail;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception exception) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), "Unknown internal server error.");
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST, "Validation failed", "Validation failed for one or more fields");
        errorDetails.setErrors(errors);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String errorMessage = ex.getMessage();
        String userMessage = extractErrorMessage(errorMessage);
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST, userMessage, "Data integrity violation.");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    private ProblemDetail handleBadCredentialsException(BadCredentialsException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "The username or password is incorrect");
        return errorDetail;
    }

    @ExceptionHandler(AccountStatusException.class)
    private ProblemDetail handleAccountStatusException(AccountStatusException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "The account is locked");
        return errorDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    private ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "You are not authorized to access this resource");
        return errorDetail;
    }

    @ExceptionHandler(SignatureException.class)
    private ProblemDetail handleSignatureException(SignatureException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "The JWT signature is invalid");
        return errorDetail;
    }

    @ExceptionHandler(ExpiredJwtException.class)
    private ProblemDetail handleExpiredJwtException(ExpiredJwtException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
        errorDetail.setProperty(DESCRIPTION, "The JWT token has expired");
        return errorDetail;
    }
}
