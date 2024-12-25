package by.clevertec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("api")
public class ApiLogicServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiLogicServerApplication.class, args);
    }
}
