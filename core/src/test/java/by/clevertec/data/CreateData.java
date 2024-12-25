package by.clevertec.data;

import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.models.News;

import java.time.Instant;
import java.util.UUID;

public class CreateData {

    private static final String TEST_TITLE = "Test title";
    private static final String TEST_TEXT = "Test text";

    public static UUID createRandomUUID() {
        return UUID.randomUUID();
    }

    public static News createNews() {

        return News.builder()
                .id(createRandomUUID())
                .title(TEST_TITLE)
                .text(TEST_TEXT)
                .time(Instant.parse("2024-12-23T10:15:30.00Z"))
                .build();
    }

    public static News updateNews() {

        return News.builder()
                .id(createRandomUUID())
                .title("New test title")
                .text("New test text")
                .time(Instant.parse("2024-12-23T10:15:30.00Z"))
                .build();
    }

    public static NewsDtoRequestUpdate updateDtoRequestNews() {

        return NewsDtoRequestUpdate.builder()
                .title("New test title")
                .text("New test text")
                .build();
    }

    public static NewsDtoRequest createNewsDtoRequest() {
        return NewsDtoRequest.builder()
                .title(TEST_TITLE)
                .text(TEST_TEXT)
                .build();
    }

    public static NewsDtoResponse createNewsDtoResponse() {
        return NewsDtoResponse.builder()
                .title(TEST_TITLE)
                .text(TEST_TEXT)
                .build();
    }
}
