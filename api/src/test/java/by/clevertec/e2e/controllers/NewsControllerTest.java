package by.clevertec.e2e.controllers;

import by.clevertec.data.TestCreateData;
import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import java.util.ArrayList;
import java.util.UUID;

import static by.clevertec.constants.Constants.NEWS;
import static by.clevertec.constants.Constants.NEWS_CREATED_SUCCESSFULLY;
import static by.clevertec.constants.Constants.NEWS_ID;
import static by.clevertec.constants.Constants.PAGE;
import static by.clevertec.constants.Constants.SIZE;
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
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void foundNewsByIdShouldReturnNews() throws Exception {

        //given
        UUID uuid = TestCreateData.createSuccessNewsUUID();

        //when then
        mockMvc.perform(get(NEWS_ID, uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.title").value(TestCreateData.createDataNewsSuccess().getTitle()))
                .andExpect(jsonPath("$.text").value(TestCreateData.createDataNewsSuccess().getText()));
    }

    @Test
    void findAllNewsShouldReturnPaginatedNews() throws Exception {

        // when & then
        mockMvc.perform(get("/news")
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(SIZE))
                .andExpect(jsonPath("$.content[0].id").isNotEmpty())
                .andExpect(jsonPath("$.content[0].title").isNotEmpty())
                .andExpect(jsonPath("$.content[0].text").isNotEmpty())
                .andExpect(jsonPath("$.totalElements").isNotEmpty())
                .andExpect(jsonPath("$.totalPages").isNotEmpty())
                .andExpect(jsonPath("$.number").value(PAGE))
                .andExpect(jsonPath("$.size").value(SIZE));
    }

    @Test
    void findAllNewsWithCommentsShouldReturnPagedResults() throws Exception {
        // given
        UUID newsId = TestCreateData.createSuccessNewsUUID();

        // when & then
        mockMvc.perform(get("/news/{id}/comments", newsId)
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.size").value(SIZE));
    }

    @Test
    void createNewsShouldReturn201AndLocationHeader() throws Exception {
        //given
        NewsDtoRequest newsDtoRequest = TestCreateData.createDataNewsSuccess();

        //when then
        mockMvc.perform(post(NEWS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsDtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith(NEWS)))
                .andExpect(content().string(org.hamcrest.Matchers.startsWith(NEWS_CREATED_SUCCESSFULLY)));
    }

    @Test
    void updateNewsShouldReturn200AndUpdatedNews() throws Exception {

        // given
        UUID uuid = TestCreateData.createSuccessNewsUUID();
        NewsDtoRequestUpdate newsDtoRequestUpdate = TestCreateData.createDataNewsDtoRequestUpdate();

        NewsDtoResponse expectedResponse = NewsDtoResponse.builder()
                .id(uuid)
                .title(newsDtoRequestUpdate.getTitle())
                .text(newsDtoRequestUpdate.getText())
                .comments(new ArrayList<>())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode expectedJson = objectMapper.valueToTree(expectedResponse);
        ((ObjectNode) expectedJson).remove("comments");

        // when then
        mockMvc.perform(put(NEWS_ID, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsDtoRequestUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson.toString()));
    }

    @Test
    void updateNewsShouldReturn404IfNotFound() throws Exception {
        //given
        UUID uuid = TestCreateData.createBadNewsUUID();

        //when then
        mockMvc.perform(put(NEWS_ID, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNewsShouldReturn204() throws Exception {

        //given
        UUID uuid = TestCreateData.createSuccessNewsUUID();

        //when then
        mockMvc.perform(delete(NEWS_ID, uuid))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNewsShouldReturn404IfNotFound() throws Exception {

        //given
        UUID uuid = TestCreateData.createBadNewsUUID();

        //when then
        mockMvc.perform(delete(NEWS_ID, uuid))
                .andExpect(status().isNotFound());
    }
}
