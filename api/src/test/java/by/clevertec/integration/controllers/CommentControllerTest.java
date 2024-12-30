package by.clevertec.integration.controllers;

import by.clevertec.controllers.CommentsController;
import by.clevertec.data.TestCreateData;
import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.request.CommentDtoRequestUpdate;
import by.clevertec.dto.response.CommentsDtoResponse;
import by.clevertec.services.CommentsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static by.clevertec.constants.Constants.COMMENTS_UUID;
import static by.clevertec.constants.Constants.COMMENT_CREATED_SUCCESSFULLY;
import static by.clevertec.constants.Constants.FIELDS;
import static by.clevertec.constants.Constants.MAX_RESULTS;
import static by.clevertec.constants.Constants.QUERY;
import static by.clevertec.constants.Constants.SORT_BY;
import static by.clevertec.constants.Constants.SORT_ORDER;
import static by.clevertec.constants.Constants.START_INDEX;
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

@WebMvcTest(CommentsController.class)
@RequiredArgsConstructor
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentsService commentsService;

    @Test
    void findComment() throws Exception {

        //given
        UUID commentId = UUID.randomUUID();
        CommentsDtoResponse commentsDtoResponse = TestCreateData.createDataCommentsDtoResponse();

        //when
        when(commentsService.findById(commentId)).thenReturn(commentsDtoResponse);

        //then
        mockMvc.perform(get(COMMENTS_UUID, commentId))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(commentsDtoResponse.getId().toString()),
                        jsonPath("$.time").value(commentsDtoResponse.getTime().toString()),
                        jsonPath("$.text").value(commentsDtoResponse.getText()),
                        jsonPath("$.username").value(commentsDtoResponse.getUsername())
                );
    }

    @Test
    void searchCommentsByTextAndUsername_shouldReturnFilteredComments() throws Exception {
        // given
        List<CommentsDtoResponse> commentsDtoResponses = List.of(
                TestCreateData.createDataCommentsDtoResponse(),
                TestCreateData.createDataCommentsDtoResponse()
        );

        //when
        when(commentsService.fullTextSearchByTextAndUsernameField(QUERY, START_INDEX, MAX_RESULTS, FIELDS, SORT_BY, SORT_ORDER))
                .thenReturn(commentsDtoResponses);

        //then
        mockMvc.perform(get("/comments/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(QUERY)
                        .param("startIndex", String.valueOf(START_INDEX))
                        .param("maxResults", String.valueOf(MAX_RESULTS))
                        .param("fields", FIELDS)
                        .param("sortBy", SORT_BY)
                        .param("sortOrder", SORT_ORDER.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(commentsDtoResponses.size()))
                .andExpect(jsonPath("$[0].username").value(commentsDtoResponses.get(0).getUsername()))
                .andExpect(jsonPath("$[0].text").value(commentsDtoResponses.get(0).getText()))
                .andExpect(jsonPath("$[1].username").value(commentsDtoResponses.get(1).getUsername()))
                .andExpect(jsonPath("$[1].text").value(commentsDtoResponses.get(1).getText()));

        verify(commentsService).fullTextSearchByTextAndUsernameField(QUERY, START_INDEX, MAX_RESULTS, FIELDS, SORT_BY, SORT_ORDER);
    }

    @Test
    void createComment() throws Exception {

        //given
        UUID commentId = UUID.randomUUID();
        CommentsDtoResponse commentsDtoResponse = TestCreateData.createDataCommentsDtoResponse();

        //when
        when(commentsService.create(eq(commentId), any(CommentDtoRequest.class))).thenReturn(commentsDtoResponse);

        //then
        mockMvc.perform(post(COMMENTS_UUID, commentId).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "text": "This is an amazing article on tech!",
                                      "username": "user123"
                                    }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/comments/" + commentsDtoResponse.getId()))
                .andExpect(content().string(COMMENT_CREATED_SUCCESSFULLY));
    }

    @Test
    void updateComment() throws Exception {

        //given
        UUID commentId = UUID.randomUUID();
        CommentsDtoResponse commentsDtoResponse = TestCreateData.createDataCommentsDtoResponse();

        //when
        when(commentsService.update(eq(commentId), any(CommentDtoRequestUpdate.class))).thenReturn(commentsDtoResponse);

        //then
        mockMvc.perform(put(COMMENTS_UUID, commentId).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "text": "Updated comment text",
                                  "username": "updatedUser"
                                }
                                """))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(commentsDtoResponse.getId().toString()),
                        jsonPath("$.time").value(commentsDtoResponse.getTime().toString()),
                        jsonPath("$.text").value(commentsDtoResponse.getText()),
                        jsonPath("$.username").value(commentsDtoResponse.getUsername())
                );
    }

    @Test
    void deleteNews() throws Exception {
        // Given
        UUID uuid = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete(COMMENTS_UUID, uuid))
                .andExpect(status().isNoContent());

        verify(commentsService).delete(uuid);
    }
}
