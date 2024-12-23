package by.clevertec.controllers;

import by.clevertec.data.TestCreateData;
import by.clevertec.data.TestCreateDataOfEntity;
import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.models.News;
import by.clevertec.services.NewsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
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
        UUID id = TestCreateData.createUUID();
        News news = new News();
        news.setId(id);
        news.setText("Some text");

        //when
        when(newsService.findById(id)).thenReturn(news);

        //then
        mockMvc.perform(get("/news/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Some text"))
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void createNews() throws Exception {
        // Given

        NewsDtoResponse newsDtoResponse = TestCreateDataOfEntity.toNewsDtoResponse();

        when(newsService.create(any(NewsDtoRequest.class))).thenReturn(newsDtoResponse);

        // When & Then
        mockMvc.perform(post("/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "title": "Test News",
                                      "text": "This is a test news article."
                                    }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/news/" + newsDtoResponse.getId()))
                .andExpect(content().string("News created successfully with id: " + newsDtoResponse.getId()));
    }


    @Test
    void updateNews() throws Exception {

        // Given
        UUID id = TestCreateData.createUUID();
        NewsDtoResponse newsDtoResponse = new NewsDtoResponse(id, UPDATED_TITLE, UPDATED_TEXT_OF_THE_NEWS);

        when(newsService.update(any(NewsDtoRequestUpdate.class), eq(id))).thenReturn(newsDtoResponse);

        // When & Then
        mockMvc.perform(put("/news/{id}", id)
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
        UUID id = TestCreateData.createUUID();

        // When & Then
        mockMvc.perform(delete("/news/{id}", id))
                .andExpect(status().isNoContent());

        verify(newsService, times(1)).delete(id);
    }
}