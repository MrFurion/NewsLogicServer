package by.clevertec.newslogicserver;

import by.clevertec.newslogicserver.env.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "by.clevertec")
@EnableCaching
public class NewsLogicServerApplication {

    public static void main(String[] args) {
        EnvLoader.loadEnv();
        SpringApplication.run(NewsLogicServerApplication.class, args);
    }
}
