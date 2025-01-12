package by.clevertec.integration.wiremock;

import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.request.CommentDtoRequestUpdate;
import by.clevertec.dto.response.CommentsDtoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.json.JSONException;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static by.clevertec.constants.TestApiConstants.APPLICATION_JSON;
import static by.clevertec.constants.TestApiConstants.COMMENTS;
import static by.clevertec.constants.TestApiConstants.CONTENT_TYPE;
import static by.clevertec.constants.TestApiConstants.LOCALHOST_8080_COMMENTS;
import static by.clevertec.constants.TestApiConstants.NOT_FOUND_STRING;
import static by.clevertec.constants.TestApiConstants.SUCCESSFULLY_CREATED_THE_COMMENT;
import static by.clevertec.util.JwtTokenUtil.buildToken;
import static by.clevertec.util.LoaderJson.loadJsonFromFile;
import static com.github.tomakehurst.wiremock.client.WireMock.created;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class CommentsControllerWiremockTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders headers = new HttpHeaders();

    @Test
    void findCommentTest() throws IOException, JSONException {
        //given
        UUID testId = UUID.fromString("c5aadb64-1db1-4e5c-91ad-1b3c1d154af8");
        String mockResponse = loadJsonFromFile("json/mockResponseForComments.json");
        String expectedJson = loadJsonFromFile("json/mockResponseForComments.json");
        String testIdStr = testId.toString();

        //when
        stubFor(get(urlPathEqualTo(COMMENTS + testIdStr))
                .willReturn(okJson(mockResponse).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
        String url = LOCALHOST_8080_COMMENTS + testIdStr;
        headers.set("Authorization", "Bearer " + buildToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expectedJson, response.getBody(), JSONCompareMode.LENIENT);
    }

    @Test
    void createCommentTest() throws IOException {
        //given
        UUID newsId = UUID.fromString("8c32ad98-6a45-4b2c-a9e4-112d8b1f9afe");
        CommentDtoRequest commentDtoRequest = new CommentDtoRequest("Test comment", "TestUser");
        String requestJson = new ObjectMapper().writeValueAsString(commentDtoRequest);

        //when
        stubFor(post(urlPathEqualTo(COMMENTS + newsId))
                .withRequestBody(equalToJson(requestJson))
                .willReturn(created()
                        .withHeader("Location", COMMENTS)
                        .withBody(SUCCESSFULLY_CREATED_THE_COMMENT)));

        headers.set("Authorization", "Bearer " + buildToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
        String createUrl = LOCALHOST_8080_COMMENTS + newsId;
        ResponseEntity<String> response = restTemplate.exchange(createUrl, HttpMethod.POST, requestEntity, String.class);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(SUCCESSFULLY_CREATED_THE_COMMENT, response.getBody());

        // Delete new created comment
        URI locationUri = response.getHeaders().getLocation();
        UUID commentId = UUID.fromString(Objects.requireNonNull(locationUri).getPath().split("/")[2]);
        stubFor(delete(urlPathEqualTo(COMMENTS + commentId))
                .willReturn(noContent()));
        String deleteUrl = LOCALHOST_8080_COMMENTS + commentId;
        headers.set("Authorization", "Bearer " + buildToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, String.class);
    }

    @Test
    void updateCommentTest() {
        //given
        UUID testId = UUID.fromString("1c01bc12-8b14-4e7f-bb4a-111c2d123abc");
        String randomText = "Updated Comment Text %s".formatted(UUID.randomUUID().toString());
        String randomUsername = "UpdatedUsername%s".formatted(UUID.randomUUID().toString());

        CommentDtoRequestUpdate commentDtoRequestUpdate = new CommentDtoRequestUpdate(randomText);
        CommentsDtoResponse commentsDtoResponse = new CommentsDtoResponse(testId, Instant.now(), randomText, randomUsername);

        String mockResponse = "{\"id\":\"" + commentsDtoResponse.getId().toString() +
                              "\", \"text\":\"" + commentsDtoResponse.getText() + "\", \"username\":\"" + commentsDtoResponse.getUsername() + "\"}";

        //when
        stubFor(put(urlPathEqualTo(COMMENTS + testId))
                .withRequestBody(equalToJson("{\"text\":\"" + randomText + "\"}"))
                .willReturn(okJson(mockResponse).withHeader(CONTENT_TYPE, APPLICATION_JSON)));
        String url = LOCALHOST_8080_COMMENTS + testId;
        headers.set("Authorization", "Bearer " + buildToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CommentDtoRequestUpdate> requestEntity = new HttpEntity<>(commentDtoRequestUpdate, headers);
        ResponseEntity<CommentsDtoResponse> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, CommentsDtoResponse.class);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testId, Objects.requireNonNull(response.getBody()).getId());
        assertEquals(randomText, response.getBody().getText());
    }

    @Test
    void deleteCommentShouldReturnNotFoundForNonExistingComment() {
        //given
        UUID nonExistentCommentId = UUID.randomUUID();

        //when
        stubFor(delete(urlPathEqualTo(COMMENTS + nonExistentCommentId))
                .willReturn(notFound()));
        String url = LOCALHOST_8080_COMMENTS + nonExistentCommentId;
        headers.set("Authorization", "Bearer " + buildToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class));

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
        Assertions.assertTrue(exception.getMessage().contains(NOT_FOUND_STRING));
    }
}
