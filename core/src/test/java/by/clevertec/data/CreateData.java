package by.clevertec.data;

import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.request.CommentDtoRequestUpdate;
import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.CommentsDtoResponse;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.models.Comment;
import by.clevertec.models.News;

import java.time.Instant;
import java.util.UUID;

public class CreateData {

    private static final String TEST_TITLE = "Test title";
    private static final String TEST_TEXT = "Test text";
    private static final String NEW_TEST_TITLE = "New test title";
    private static final String NEW_TEST_TEXT = "New test text";
    private static final String TIME_ZONE = "2024-12-23T10:15:30.00Z";
    private static final String USER_TEST = "UserTest";
    private static final String USER_UPDATE_TEST = "UserTest2";
    private static final String NEWS_UUID_TEST = "be3429c5-daae-4b6e-93fc-dcb1e5c9911b";

    public static UUID createRandomUUID() {
        return UUID.randomUUID();
    }

    public static News createNews() {

        return News.builder()
                .id(createRandomUUID())
                .title(TEST_TITLE)
                .text(TEST_TEXT)
                .time(Instant.parse(TIME_ZONE))
                .build();
    }

    public static News updateNews() {

        return News.builder()
                .id(createRandomUUID())
                .title(NEW_TEST_TITLE)
                .text(NEW_TEST_TEXT)
                .time(Instant.parse(TIME_ZONE))
                .build();
    }

    public static NewsDtoRequestUpdate updateDtoRequestNews() {

        return NewsDtoRequestUpdate.builder()
                .title(NEW_TEST_TITLE)
                .text(NEW_TEST_TEXT)
                .build();
    }

    public static CommentDtoRequestUpdate updateDtoRequestComment() {
        return CommentDtoRequestUpdate.builder()
                .text(NEW_TEST_TEXT)
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

    public static Comment createComment(){
        return Comment.builder()
                .id(createRandomUUID())
                .time(Instant.parse(TIME_ZONE))
                .text(TEST_TEXT)
                .username(USER_TEST)
                .news(News.builder()
                        .id(UUID.fromString(NEWS_UUID_TEST))
                        .build())
                .build();
    }

    public static Comment updateComment(){
        return Comment.builder()
                .id(createRandomUUID())
                .time(Instant.parse(TIME_ZONE))
                .text(NEW_TEST_TEXT)
                .username(USER_UPDATE_TEST)
                .news(News.builder()
                        .id(UUID.fromString(NEWS_UUID_TEST))
                        .build())
                .build();
    }

    public static CommentsDtoResponse createCommentsDtoResponse(){
        return CommentsDtoResponse.builder()
                .text(TEST_TEXT)
                .username(USER_TEST)
                .build();
    }

    public static CommentDtoRequest createCommentDtoRequest(){
        return CommentDtoRequest.builder()
                .text(TEST_TEXT)
                .username(USER_TEST)
                .build();
    }
}
