package by.clevertec.controllers;

import by.clevertec.annotation.MonitorPerformance;
import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.services.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static by.clevertec.constants.Constants.APPLICATION_JSON;
import static by.clevertec.constants.Constants.BAD_REQUEST;
import static by.clevertec.constants.Constants.CREATED;
import static by.clevertec.constants.Constants.FIELDS;
import static by.clevertec.constants.Constants.FIELDS_TO_SEARCH_IN_E_G_TITLE_TEXT_ETC_DEFAULT_IS_TITLE;
import static by.clevertec.constants.Constants.FIELD_BY_WHICH_TO_SORT_THE_RESULTS_DEFAULT_IS_SORT_TITLE;
import static by.clevertec.constants.Constants.ID;
import static by.clevertec.constants.Constants.INTERNAL_SERVER_ERROR;
import static by.clevertec.constants.Constants.INTERNAL_SERVER_ERROR_REPORT;
import static by.clevertec.constants.Constants.INVALID_INPUT_PARAMETERS;
import static by.clevertec.constants.Constants.INVALID_REQUEST_BODY;
import static by.clevertec.constants.Constants.LOCATION;
import static by.clevertec.constants.Constants.MAXIMUM_NUMBER_OF_RESULTS;
import static by.clevertec.constants.Constants.MAX_RESULTS;
import static by.clevertec.constants.Constants.NEWS_NOT_FOUND;
import static by.clevertec.constants.Constants.NOT_FOUND;
import static by.clevertec.constants.Constants.NO_CONTENT;
import static by.clevertec.constants.Constants.OK;
import static by.clevertec.constants.Constants.PAGE;
import static by.clevertec.constants.Constants.QUERY;
import static by.clevertec.constants.Constants.QUERY_STRING_TO_SEARCH_FOR_IN_NEWS_TITLES_AND_TEXT;
import static by.clevertec.constants.Constants.RETRIEVED_THE_SEARCH_RESULTS;
import static by.clevertec.constants.Constants.SIZE;
import static by.clevertec.constants.Constants.SORT_BY;
import static by.clevertec.constants.Constants.SORT_ORDER;
import static by.clevertec.constants.Constants.SORT_ORDER_ASC_FOR_ASCENDING;
import static by.clevertec.constants.Constants.STARTING_INDEX_OF_THE_RESULTS_DEFAULT;
import static by.clevertec.constants.Constants.START_INDEX;
import static by.clevertec.constants.Constants.SUCCESSFULLY_RETRIEVED_THE_NEWS_ITEM;
import static by.clevertec.constants.Constants.TEXT_PLAIN;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    public static final String SUCCESSFULLY_DELETED_THE_NEWS = "Successfully deleted the news";

    private final NewsService newsService;

    @Operation(
            summary = "Find News by ID",
            description = "Fetches the news item based on the provided unique identifier (UUID).",
            parameters = {
                    @Parameter(name = ID, description = "The unique identifier of the news item.", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = OK, description = SUCCESSFULLY_RETRIEVED_THE_NEWS_ITEM,
                            content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = NewsDtoResponse.class))),
                    @ApiResponse(responseCode = NOT_FOUND, description = NEWS_NOT_FOUND,
                            content = @Content(mediaType = APPLICATION_JSON))
            })
    @MonitorPerformance
    @GetMapping("/{id}")
    public ResponseEntity<NewsDtoResponse> findNews(@PathVariable UUID id) {
        NewsDtoResponse newsDtoResponse = newsService.findById(id);
        return ResponseEntity.ok(newsDtoResponse);
    }

    @Operation(
            summary = "Get all News items",
            description = "Fetches all news items, with support for pagination. You can specify the page and size of the results.",
            parameters = {
                    @Parameter(name = PAGE, description = "Page number to retrieve (0-based)", in = ParameterIn.QUERY),
                    @Parameter(name = SIZE, description = "Number of items per page", in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = OK, description = "Successfully retrieved the list of news items",
                            content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = BAD_REQUEST, description = "Invalid pagination parameters")
            })
    @GetMapping
    public ResponseEntity<Page<NewsDtoResponse>> findAllNews(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NewsDtoResponse> newsDtoResponses = newsService.findAll(pageable);
        return ResponseEntity.ok(newsDtoResponses);
    }

    @Operation(
            summary = "Search news by title and text",
            description = "Performs a full-text search for news items based on the query string. Supports pagination, field selection, and sorting.",
            parameters = {
                    @Parameter(name = QUERY, description = QUERY_STRING_TO_SEARCH_FOR_IN_NEWS_TITLES_AND_TEXT, required = true),
                    @Parameter(name = START_INDEX, description = STARTING_INDEX_OF_THE_RESULTS_DEFAULT, in = ParameterIn.QUERY),
                    @Parameter(name = MAX_RESULTS, description = MAXIMUM_NUMBER_OF_RESULTS, in = ParameterIn.QUERY),
                    @Parameter(name = FIELDS, description = FIELDS_TO_SEARCH_IN_E_G_TITLE_TEXT_ETC_DEFAULT_IS_TITLE, in = ParameterIn.QUERY),
                    @Parameter(name = SORT_BY, description = FIELD_BY_WHICH_TO_SORT_THE_RESULTS_DEFAULT_IS_SORT_TITLE, in = ParameterIn.QUERY),
                    @Parameter(name = SORT_ORDER, description = SORT_ORDER_ASC_FOR_ASCENDING, in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = OK, description = RETRIEVED_THE_SEARCH_RESULTS,
                            content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = NewsDtoResponse.class))),
                    @ApiResponse(responseCode = BAD_REQUEST, description = INVALID_INPUT_PARAMETERS),
                    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR_REPORT)
            })
    @GetMapping("/search")
    public ResponseEntity<List<NewsDtoResponse>> searchNewsByTitleAndText(@RequestBody String query,
                                                                          @RequestParam(defaultValue = "0") int startIndex,
                                                                          @RequestParam(defaultValue = "5") int maxResults,
                                                                          @RequestParam(defaultValue = "title") String fields,
                                                                          @RequestParam(defaultValue = "sort_title") String sortBy,
                                                                          @RequestParam(defaultValue = "ASC") SortOrder sortOrder) {

        List<NewsDtoResponse> newsDtoResponses = newsService.fullTextSearchByTitleAndTextField(query, startIndex, maxResults,
                fields, sortBy, sortOrder);
        return ResponseEntity.ok(newsDtoResponses);
    }

    @Operation(
            summary = "Get all comments for a News item",
            description = "Retrieves a paginated list of comments associated with a specific news item, identified by its ID.",
            parameters = {
                    @Parameter(name = ID, description = "The UUID of the news item for which comments are retrieved", required = true, in = ParameterIn.PATH),
                    @Parameter(name = PAGE, description = "The page number for pagination (default is 0)", in = ParameterIn.QUERY),
                    @Parameter(name = SIZE, description = "The number of items per page for pagination (default is 5)", in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = OK, description = "Successfully retrieved the comments",
                            content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = NOT_FOUND, description = NEWS_NOT_FOUND),
                    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR_REPORT)
            })
    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<NewsDtoResponse>> findAllNewsWithComments(@PathVariable UUID id,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "5") int size) {
        Page<NewsDtoResponse> newsDtoResponse = newsService.findByIdWithAllComments(id, page, size);
        return ResponseEntity.ok(newsDtoResponse);
    }

    @Operation(
            summary = "Create a new News item",
            description = "Creates a new news item based on the provided request body.",
            responses = {
                    @ApiResponse(responseCode = CREATED, description = "News item created successfully",
                            headers = @Header(name = LOCATION, description = "The URI of the newly created news item"),
                            content = @Content(mediaType = TEXT_PLAIN)),
                    @ApiResponse(responseCode = BAD_REQUEST, description = INVALID_REQUEST_BODY),
                    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR_REPORT)
            })

    @MonitorPerformance
    @PostMapping
    public ResponseEntity<String> createNews(@Validated @RequestBody NewsDtoRequest newsDtoRequest) {
        NewsDtoResponse newsDtoResponse = newsService.create(newsDtoRequest);
        URI location = URI.create("/news/" + newsDtoResponse.getId());
        return ResponseEntity.created(location).body("News created successfully");
    }

    @Operation(
            summary = "Update an existing News item",
            description = "Updates an existing news item identified by its ID with the provided details.",
            parameters = {
                    @Parameter(name = ID, description = "The UUID of the news item to be updated", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = OK, description = "Successfully updated the news item",
                            content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = NewsDtoResponse.class))),
                    @ApiResponse(responseCode = BAD_REQUEST, description = INVALID_INPUT_PARAMETERS),
                    @ApiResponse(responseCode = NOT_FOUND, description = NEWS_NOT_FOUND),
                    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR_REPORT)
            })
    @PutMapping("/{id}")
    public ResponseEntity<NewsDtoResponse> updateNews(@Validated @RequestBody NewsDtoRequestUpdate newsDtoRequestUpdate,
                                                      @PathVariable UUID id) {

        NewsDtoResponse newsDtoResponse = newsService.update(newsDtoRequestUpdate, id);
        return ResponseEntity.ok(newsDtoResponse);
    }

    @Operation(
            summary = "Delete a News item",
            description = "Deletes a news item identified by its ID.",
            parameters = {
                    @Parameter(name = ID, description = "The UUID of the news item to be deleted", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = NO_CONTENT, description = SUCCESSFULLY_DELETED_THE_NEWS),
                    @ApiResponse(responseCode = NOT_FOUND, description = NEWS_NOT_FOUND),
                    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR_REPORT)
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNews(@PathVariable UUID id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
