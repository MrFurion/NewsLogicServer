package by.clevertec.e2e.controllers;

import by.clevertec.data.TestCreateData;
import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.request.CommentDtoRequestUpdate;
import by.clevertec.dto.response.CommentsDtoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static by.clevertec.constants.TestApiConstants.COMMENTS_UUID;
import static by.clevertec.constants.TestApiConstants.COMMENT_CREATED_SUCCESSFULLY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@ActiveProfiles("testcontainers")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:db/data.sql"})
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void foundCommentByIdShouldReturnComment() throws Exception {

        //given
        UUID uuid = TestCreateData.createSuccessCommentsUUID();

        //when then
        mockMvc.perform(get(COMMENTS_UUID, uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.text").value(TestCreateData.createDataCommentsResponseSuccess().getText()))
                .andExpect(jsonPath("$.username").value(TestCreateData.createDataCommentsResponseSuccess().getUsername()));
    }

    @Test
    void createCommentShouldReturn201AndLocationHeader() throws Exception {

        //given
        UUID uuid = TestCreateData.createSuccessNewsUUID();
        CommentDtoRequest commentDtoRequest = TestCreateData.createDataCommentDtoRequestSuccess();

        //when then
        mockMvc.perform(post(COMMENTS_UUID, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern("^/comments/[0-9a-fA-F-]{36}$")))
                .andExpect(content().string(org.hamcrest.Matchers.startsWith(COMMENT_CREATED_SUCCESSFULLY)));
    }

    @Test
    void updateCommentShouldReturn200AndUpdatedComment() throws Exception {

        //given
        UUID uuid = TestCreateData.createSuccessCommentsUUID();
        CommentDtoRequestUpdate commentDtoRequestUpdate = TestCreateData.createDataCommentDtoRequestUpdate();

        CommentsDtoResponse expectedResponse = TestCreateData.createDataCommentsResponseSuccess();
        expectedResponse.setTime(Instant.parse("2024-01-02T00:00:00Z"));

        //when then
        mockMvc.perform(put(COMMENTS_UUID, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDtoRequestUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    void updateCommentShouldReturn404IfNotFound() throws Exception {
        //given
        UUID uuid = TestCreateData.createBadCommentsUUID();

        //when then
        mockMvc.perform(put(COMMENTS_UUID, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCommentShouldReturn204() throws Exception {

        //given
        UUID uuid = TestCreateData.createSuccessCommentsUUID();

        //when then
        mockMvc.perform(delete(COMMENTS_UUID, uuid))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCommentShouldReturn404IfNotFound() throws Exception {

        //given
        UUID uuid = TestCreateData.createBadCommentsUUID();

        //when then
        mockMvc.perform(delete(COMMENTS_UUID, uuid))
                .andExpect(status().isNotFound());
    }

}
