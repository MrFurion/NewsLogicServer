package by.clevertec.newslogicserver.env;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    public static void loadEnv() {
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            if (System.getenv(key) == null) {
                System.setProperty(key, value);
            }
        });
    }
}