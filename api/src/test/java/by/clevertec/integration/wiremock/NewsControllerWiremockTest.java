package by.clevertec.integration.wiremock;

import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
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
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static by.clevertec.constants.TestApiConstants.APPLICATION_JSON;
import static by.clevertec.constants.TestApiConstants.CONTENT_TYPE;
import static by.clevertec.constants.TestApiConstants.HTTP_LOCALHOST_8080_NEWS;
import static by.clevertec.constants.TestApiConstants.NEWS_CREATED_SUCCESSFULLY;
import static by.clevertec.constants.TestApiConstants.NOT_FOUND_STRING;
import static by.clevertec.constants.TestApiConstants.PAGE_STRING;
import static by.clevertec.constants.TestApiConstants.SIZE_STRING;
import static by.clevertec.util.ExtractNews.extractNewsIdFromResponse;
import static by.clevertec.util.ExtractNews.newsIdForDelete;
import static by.clevertec.util.JwtTokenUtil.buildToken;
import static by.clevertec.util.LoaderJson.loadJsonFromFile;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@WireMockTest
class NewsControllerWiremockTest {

    private static final int SIZE = 5;
    private static final int PAGE = 1;
    private static final String NEWS = "/news/";

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    @Test
    void findNewsTest() throws JSONException, IOException {
        // given
        UUID testId = UUID.fromString("c5aadb64-1db1-4e5c-91ad-1b3c1d154af1");
        String expectedJson = loadJsonFromFile("json/expectedJsonForNews.json");
        String mockResponse = loadJsonFromFile("json/mockResponseForNews.json");
        String testIdStr = testId.toString();

        // when
        stubFor(get(urlPathEqualTo(NEWS + testIdStr))
                .willReturn(okJson(mockResponse)));

        headers.set("Authorization", "Bearer " + buildToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = HTTP_LOCALHOST_8080_NEWS + testIdStr;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expectedJson, response.getBody(), JSONCompareMode.LENIENT);
    }

    @Test
    void findAllNewsShouldReturnPagedNewsTest() {
        //given when
        stubFor(get(urlPathEqualTo("/news"))
                .withQueryParam(PAGE_STRING, equalTo("0"))
                .withQueryParam(SIZE_STRING, equalTo("5"))
                .willReturn(ok().withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        String url = "http://localhost:8080/news?page=0&size=5";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(APPLICATION_JSON, Objects.requireNonNull(response.getHeaders().getContentType()).toString());
    }

    @Test
    void findAllNewsWithCommentsShouldReturnCommentsTest() {
        //given
        UUID uuid = UUID.fromString("c5aadb64-1db1-4e5c-91ad-1b3c1d154af1");
        String mockResponse = "[{\"id\": \"" + uuid + "\", \"title\": \"News 1\", \"text\": \"Text 1\"}]";

        //when
        stubFor(get(urlPathMatching("/news/.+/comments"))
                .withQueryParam(PAGE_STRING, equalTo(String.valueOf(PAGE)))
                .withQueryParam(SIZE_STRING, equalTo(String.valueOf(SIZE)))
                .willReturn(okJson(mockResponse).withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        String url = HTTP_LOCALHOST_8080_NEWS + uuid + "/comments?page=" + PAGE + "&size=" + SIZE;

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(APPLICATION_JSON, Objects.requireNonNull(response.getHeaders().getContentType()).toString());
    }

    @Test
    void createNewsShouldReturnCreatedResponseTest() {
        //given
        String randomTitle = "News Title " + UUID.randomUUID();
        String randomText = "News Text " + UUID.randomUUID();
        NewsDtoRequest newsDtoRequest = new NewsDtoRequest(randomTitle, randomText);
        NewsDtoResponse newsDtoResponse = new NewsDtoResponse(UUID.randomUUID(), randomTitle, randomText, new ArrayList<>());

        String mockResponse = "{\"id\":\"" + newsDtoResponse.getId().toString() + "\", \"title\":\""
                              + newsDtoResponse.getTitle() + "\", \"text\":\"" + newsDtoResponse.getText() + "\"}";

        //when
        stubFor(post(urlPathEqualTo("/news"))
                .withRequestBody(equalToJson("{\"title\":\"News Title\",\"text\":\"News Text\"}"))
                .willReturn(okJson(mockResponse).withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        String url = "http://localhost:8080/news";
        headers.set("Authorization", "Bearer " + buildToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NewsDtoRequest> requestEntity = new HttpEntity<>(newsDtoRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains(NEWS_CREATED_SUCCESSFULLY));


        //Delete new create news
        extractNewsIdFromResponse(response);
        String deleteUrl = HTTP_LOCALHOST_8080_NEWS + newsIdForDelete;
        HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);

        stubFor(delete(urlPathEqualTo(NEWS + newsIdForDelete))
                .willReturn(noContent()));
        restTemplate.exchange(deleteUrl, HttpMethod.DELETE, deleteRequest, String.class);
    }

    @Test
    void updateNewsShouldReturnUpdatedNewsTest() {
        //given
        UUID newsId = UUID.fromString("2a23ad56-b7cd-4c9d-a5e2-124b2d1e9def");
        String randomTitle = "Title %s".formatted(UUID.randomUUID().toString());
        String randomText = "Update Text%s".formatted(UUID.randomUUID().toString());

        NewsDtoRequestUpdate newsDtoRequestUpdate = new NewsDtoRequestUpdate(randomTitle, randomText);
        NewsDtoResponse newsDtoResponse = new NewsDtoResponse(newsId, randomTitle, randomText, new ArrayList<>());

        String mockResponse = "{\"id\":\"" + newsDtoResponse.getId().toString() + "\", \"title\":\"" + newsDtoResponse.getTitle() + "\", \"text\":\"" + newsDtoResponse.getText() + "\"}";

        //when
        stubFor(put(urlPathEqualTo(NEWS + newsId))
                .withRequestBody(equalToJson("{\"title\":\"" + randomTitle + "\",\"text\":\"" + randomText + "\"}"))
                .willReturn(okJson(mockResponse).withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        String url = HTTP_LOCALHOST_8080_NEWS + newsId;

        headers.set("Authorization", "Bearer " + buildToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NewsDtoRequestUpdate> requestEntity = new HttpEntity<>(newsDtoRequestUpdate, headers);
        ResponseEntity<NewsDtoResponse> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, NewsDtoResponse.class);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newsId, Objects.requireNonNull(response.getBody()).getId());
        assertEquals(randomTitle, response.getBody().getTitle());
        assertEquals(randomText, response.getBody().getText());
    }

    @Test
    void deleteNewsShouldReturnNotFoundForNonExistingNews() {
        //given
        UUID nonExistentNewsId = UUID.randomUUID();

        //when
        stubFor(delete(urlPathEqualTo(NEWS + nonExistentNewsId))
                .willReturn(notFound()));

        String url = HTTP_LOCALHOST_8080_NEWS + nonExistentNewsId;

        headers.set("Authorization", "Bearer " + buildToken());
        HttpEntity<NewsDtoRequestUpdate> requestEntity = new HttpEntity<>(headers);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class));

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode().value());
        Assertions.assertTrue(exception.getMessage().contains(NOT_FOUND_STRING));
    }
}
