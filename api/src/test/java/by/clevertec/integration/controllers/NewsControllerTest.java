package by.clevertec.integration.controllers;

import by.clevertec.controllers.NewsController;
import by.clevertec.data.TestCreateData;
import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.models.Comment;
import by.clevertec.services.NewsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static by.clevertec.constants.Constants.FIELDS;
import static by.clevertec.constants.Constants.MAX_RESULTS;
import static by.clevertec.constants.Constants.NEWS;
import static by.clevertec.constants.Constants.NEWS_CREATED_SUCCESSFULLY;
import static by.clevertec.constants.Constants.NEWS_ID;
import static by.clevertec.constants.Constants.PAGE;
import static by.clevertec.constants.Constants.QUERY;
import static by.clevertec.constants.Constants.SIZE;
import static by.clevertec.constants.Constants.SORT_BY;
import static by.clevertec.constants.Constants.SORT_ORDER;
import static by.clevertec.constants.Constants.START_INDEX;
import static by.clevertec.constants.Constants.UPDATED_TEXT_OF_THE_NEWS;
import static by.clevertec.constants.Constants.UPDATED_TITLE;
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
    void findAllNews_shouldReturnPagedNews() throws Exception {

        // given
        NewsDtoResponse news1 = TestCreateData.createDataNewsDtoResponse();
        NewsDtoResponse news2 = TestCreateData.createDataNewsDtoResponse();

        List<NewsDtoResponse> newsList = List.of(news1, news2);
        Page<NewsDtoResponse> newsPage = new PageImpl<>(newsList, PageRequest.of(PAGE, SIZE), newsList.size());

        // when
        when(newsService.findAll(PageRequest.of(PAGE, SIZE))).thenReturn(newsPage);

        // then
        mockMvc.perform(get("/news")
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(news1.getId().toString()))
                .andExpect(jsonPath("$.content[0].title").value(news1.getTitle()))
                .andExpect(jsonPath("$.content[0].text").value(news1.getText()))
                .andExpect(jsonPath("$.content[1].id").value(news2.getId().toString()))
                .andExpect(jsonPath("$.content[1].title").value(news2.getTitle()))
                .andExpect(jsonPath("$.content[1].text").value(news2.getText()))
                .andExpect(jsonPath("$.size").value(SIZE))
                .andExpect(jsonPath("$.number").value(PAGE));
    }

    @Test
    void searchNewsByTitleAndText_shouldReturnSearchedNews() throws Exception {
        // given
        NewsDtoResponse news1 = TestCreateData.createDataNewsDtoResponse();
        NewsDtoResponse news2 = TestCreateData.createDataNewsDtoResponse();
        List<NewsDtoResponse> expectedNewsList = List.of(news1, news2);

        // when
        when(newsService.fullTextSearchByTitleAndTextField(QUERY, START_INDEX, MAX_RESULTS, FIELDS, SORT_BY, SORT_ORDER))
                .thenReturn(expectedNewsList);

        // then
        mockMvc.perform(get("/news/search")
                        .content(QUERY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startIndex", String.valueOf(START_INDEX))
                        .param("maxResults", String.valueOf(MAX_RESULTS))
                        .param("fields", FIELDS)
                        .param("sortBy", SORT_BY)
                        .param("sortOrder", SORT_ORDER.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedNewsList.size()))
                .andExpect(jsonPath("$[0].id").value(news1.getId().toString()))
                .andExpect(jsonPath("$[0].title").value(news1.getTitle()))
                .andExpect(jsonPath("$[0].text").value(news1.getText()))
                .andExpect(jsonPath("$[1].id").value(news2.getId().toString()))
                .andExpect(jsonPath("$[1].title").value(news2.getTitle()))
                .andExpect(jsonPath("$[1].text").value(news2.getText()));

        verify(newsService).fullTextSearchByTitleAndTextField(QUERY, START_INDEX, MAX_RESULTS, FIELDS, SORT_BY, SORT_ORDER);
    }

    @Test
    void findAllNewsWithComments_shouldReturnNewsWithComments() throws Exception {
        // given
        UUID newsId = UUID.randomUUID();
        Page<NewsDtoResponse> newsPage = new PageImpl<>(List.of(
                TestCreateData.createDataNewsDtoResponse(),
                TestCreateData.createDataNewsDtoResponse()
        ));

        when(newsService.findByIdWithAllComments(newsId, PAGE, SIZE)).thenReturn(newsPage);

        // then
        mockMvc.perform(get("/news/{id}/comments", newsId)
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(newsPage.getContent().size()))
                .andExpect(jsonPath("$.content[0].id").value(newsPage.getContent().get(0).getId().toString()))
                .andExpect(jsonPath("$.content[0].title").value(newsPage.getContent().get(0).getTitle()))
                .andExpect(jsonPath("$.content[1].id").value(newsPage.getContent().get(1).getId().toString()))
                .andExpect(jsonPath("$.content[1].title").value(newsPage.getContent().get(1).getTitle()));

        verify(newsService).findByIdWithAllComments(newsId, PAGE, SIZE);
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
        List<Comment> comments = new ArrayList<>();
        NewsDtoResponse newsDtoResponse = new NewsDtoResponse(id, UPDATED_TITLE, UPDATED_TEXT_OF_THE_NEWS, comments);

        // When
        when(newsService.update(any(NewsDtoRequestUpdate.class), eq(id))).thenReturn(newsDtoResponse);

        //Then
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