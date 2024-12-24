package by.clevertec.e2e.controllers;

import by.clevertec.data.TestCreateData;
import by.clevertec.data.TestCreateDataOfEntity;
import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
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

import java.util.UUID;

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
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:db/data.sql"})

public class NewsControllerTest {

    private static final String NEWS_ID = "/news/{id}";
    public static final String NEWS_CREATED_SUCCESSFULLY_WITH_ID = "News created successfully with id: ";
    public static final String NEWS = "/news";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void foundNewsByIdShouldReturnNews() throws Exception {

        //given
        UUID uuid = TestCreateData.createSuccessUUID();

        //when then
        mockMvc.perform(get(NEWS_ID, uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.title").value(TestCreateDataOfEntity.toNewsSuccess().getTitle()))
                .andExpect(jsonPath("$.text").value(TestCreateDataOfEntity.toNewsSuccess().getText()));
    }

    @Test
    void createNewsShouldReturn201AndLocationHeader() throws Exception {
        //given
        NewsDtoRequest newsDtoRequest = TestCreateDataOfEntity.toNewsSuccess();

        //when then
        mockMvc.perform(post(NEWS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsDtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith(NEWS)))
                .andExpect(content().string(org.hamcrest.Matchers.startsWith(NEWS_CREATED_SUCCESSFULLY_WITH_ID)));
    }

    @Test
    void updateNewsShouldReturn200AndUpdatedNews() throws Exception {

        //given
        UUID uuid = TestCreateData.createSuccessUUID();
        NewsDtoRequestUpdate newsDtoRequestUpdate = TestCreateDataOfEntity.toNewsDtoRequestUpdate();

        NewsDtoResponse expectedResponse = NewsDtoResponse.builder()
                .id(uuid)
                .title(newsDtoRequestUpdate.getTitle())
                .text(newsDtoRequestUpdate.getText())
                .build();

        //when then
        mockMvc.perform(put(NEWS_ID, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsDtoRequestUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    void updateNewsShouldReturn404IfNotFound() throws Exception {
        //given
        UUID uuid = TestCreateData.createBadUUID();

        //when then
        mockMvc.perform(put(NEWS_ID, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNewsShouldReturn204() throws Exception {

        //given
        UUID uuid = TestCreateData.createSuccessUUID();

        //when then
        mockMvc.perform(delete(NEWS_ID, uuid))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNewsShouldReturn404IfNotFound() throws Exception {

        //given
        UUID uuid = TestCreateData.createBadUUID();

        //when then
        mockMvc.perform(delete(NEWS_ID, uuid))
                .andExpect(status().isNotFound());
    }
}
