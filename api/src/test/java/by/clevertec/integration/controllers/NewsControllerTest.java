package by.clevertec.integration.controllers;

import by.clevertec.controllers.NewsController;
import by.clevertec.data.TestCreateData;
import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.services.NewsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static by.clevertec.constants.Constants.NEWS;
import static by.clevertec.constants.Constants.NEWS_CREATED_SUCCESSFULLY;
import static by.clevertec.constants.Constants.NEWS_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NewsController.class)
@RequiredArgsConstructor
class NewsControllerTest {

    private static final String UPDATED_TITLE = "Updated Title";
    private static final String UPDATED_TEXT_OF_THE_NEWS = "Updated text of the news.";

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private NewsService newsService;


    @Test
    void findNews() throws Exception {

        // given
        NewsDtoResponse newsDtoResponse = TestCreateData.createDataNewsDtoResponse();

        //when
        when(newsService.findById(newsDtoResponse.getId())).thenReturn(newsDtoResponse);

        //then
        mockMvc.perform(get(NEWS_ID, newsDtoResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(newsDtoResponse.getTitle()))
                .andExpect(jsonPath("$.text").value(newsDtoResponse.getText()))
                .andExpect(jsonPath("$.id").value(newsDtoResponse.getId().toString()));
    }

    @Test
    void createNews() throws Exception {

        // Given
        NewsDtoResponse newsDtoResponse = TestCreateData.createDataNewsDtoResponse();

        // When
        when(newsService.create(any(NewsDtoRequest.class))).thenReturn(newsDtoResponse);

        //Then
        mockMvc.perform(post(NEWS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "title": "Test News",
                                      "text": "This is a test news article."
                                    }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/news/" + newsDtoResponse.getId()))
                .andExpect(content().string(NEWS_CREATED_SUCCESSFULLY));
    }


    @Test
    void updateNews() throws Exception {

        // Given
        UUID id = UUID.randomUUID();
        NewsDtoResponse newsDtoResponse = new NewsDtoResponse(id, UPDATED_TITLE, UPDATED_TEXT_OF_THE_NEWS);

        when(newsService.update(any(NewsDtoRequestUpdate.class), eq(id))).thenReturn(newsDtoResponse);

        // When & Then
        mockMvc.perform(put(NEWS_ID, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "title": "Updated Title",
                                      "text": "Updated text of the news."
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value(UPDATED_TITLE))
                .andExpect(jsonPath("$.text").value(UPDATED_TEXT_OF_THE_NEWS));
    }

    @Test
    void deleteNews() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete(NEWS_ID, id))
                .andExpect(status().isNoContent());

        verify(newsService).delete(id);
    }
}