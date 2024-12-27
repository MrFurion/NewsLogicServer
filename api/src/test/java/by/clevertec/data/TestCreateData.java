package by.clevertec.data;

import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.request.CommentDtoRequestUpdate;
import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.CommentsDtoResponse;
import by.clevertec.dto.response.NewsDtoResponse;

import java.time.Instant;
import java.util.UUID;

public class TestCreateData {
    public static UUID createSuccessNewsUUID(){
        return UUID.fromString("1e1a3208-dfc5-4eb7-8d8e-1f50f31dc70a");
    }

    public static UUID createSuccessCommentsUUID(){
        return UUID.fromString("b71e4c96-9d6f-450d-81c6-3016efddc84e");
    }

    public static UUID createBadNewsUUID() {
        return UUID.fromString("2e1a3208-dfc5-4eb7-8d8e-1f50f31dc70a");
    }

    public static UUID createBadCommentsUUID() {
        return UUID.fromString("b11e4c96-9d6f-450d-81c6-3016efddc84e");
    }

    public static NewsDtoResponse createDataNewsDtoResponse() {
        return NewsDtoResponse.builder()
                .id(UUID.randomUUID())
                .title("Test News")
                .text("This is a test news article.")
                .build();
    }

    public static CommentsDtoResponse createDataCommentsDtoResponse() {
        return CommentsDtoResponse.builder()
                .id(UUID.randomUUID())
                .time(Instant.parse("2024-01-01T10:00:00Z"))
                .text("This is an amazing article on tech!")
                .username("user123")
                .build();
    }

    public static CommentsDtoResponse createDataCommentsResponseSuccess(){
        return CommentsDtoResponse.builder()
                .id(createSuccessCommentsUUID())
                .text("Great championship coverage!")
                .username("sportsfan")
                .build();
    }

    public static CommentDtoRequest createDataCommentDtoRequestSuccess() {
        return CommentDtoRequest.builder()
                .text("Comment text success")
                .username("Jerry")
                .build();
    }

    public static NewsDtoRequest createDataNewsSuccess(){
        return NewsDtoRequest.builder()
                .title("Tech Innovations 2024")
                .text("Breaking news about new tech innovations.")
                .build();
    }

    public static NewsDtoRequestUpdate createDataNewsDtoRequestUpdate(){
        return NewsDtoRequestUpdate.builder()
                .title("Updated Tech Innovations 2024")
                .text("Updated breaking news about tech innovations.")
                .build();
    }

    public static CommentDtoRequestUpdate createDataCommentDtoRequestUpdate(){
        return CommentDtoRequestUpdate.builder()
                .text("Great championship coverage!")
                .build();
    }
}
