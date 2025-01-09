package by.clevertec.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private HttpStatus status;
    private String message;
    private String description;
    private Map<String, String> errors;

    public ErrorDetails(HttpStatus status, String message, String description) {
        this.status = status;
        this.message = message;
        this.description = description;
        this.errors = new HashMap<>();
    }

    public void addError(String field, String message) {
        this.errors.put(field, message);
    }
}