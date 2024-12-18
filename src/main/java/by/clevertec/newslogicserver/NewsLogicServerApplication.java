package by.clevertec.newslogicserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "by.clevertec")
public class NewsLogicServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsLogicServerApplication.class, args);
	}
}
