package by.clevertec.util;

import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoaderJson {

    public static String loadJsonFromFile(String filename) throws IOException {
        File file = ResourceUtils.getFile("classpath:" + filename);
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }
}
