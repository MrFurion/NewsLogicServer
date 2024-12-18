package by.clevertec.newslogicserver;

import by.clevertec.newslogicserver.env.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "by.clevertec")
public class NewsLogicServerApplication {

	public static void main(String[] args) {
		EnvLoader.loadEnv();
		SpringApplication.run(NewsLogicServerApplication.class, args);
	}
}
