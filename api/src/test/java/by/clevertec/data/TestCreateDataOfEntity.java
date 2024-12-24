package by.clevertec.data;

import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;

import java.util.UUID;

public class TestCreateDataOfEntity {

    public static NewsDtoResponse toNewsDtoResponse() {
        return NewsDtoResponse.builder()
                .id(UUID.randomUUID())
                .title("Test News")
                .text("This is a test news article.")
                .build();
    }

    public static NewsDtoRequest toNewsSuccess(){
        return NewsDtoRequest.builder()
                .title("Tech Innovations 2024")
                .text("Breaking news about new tech innovations.")
                .build();
    }

    public static NewsDtoRequestUpdate toNewsDtoRequestUpdate(){
        return NewsDtoRequestUpdate.builder()
                .title("Updated Tech Innovations 2024")
                .text("Updated breaking news about tech innovations.")
                .build();
    }
}
