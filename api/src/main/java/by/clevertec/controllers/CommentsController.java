package by.clevertec.controllers;

import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.request.CommentDtoRequestUpdate;
import by.clevertec.dto.response.CommentsDtoResponse;
import by.clevertec.services.CommentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
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
import static by.clevertec.constants.Constants.COMMENT_NOT_FOUND;
import static by.clevertec.constants.Constants.CREATED;
import static by.clevertec.constants.Constants.FIELDS;
import static by.clevertec.constants.Constants.INTERNAL_SERVER_ERROR;
import static by.clevertec.constants.Constants.INTERNAL_SERVER_ERROR_REPORT;
import static by.clevertec.constants.Constants.INVALID_INPUT_DATA;
import static by.clevertec.constants.Constants.INVALID_QUERY_PARAMETERS;
import static by.clevertec.constants.Constants.MAX_RESULTS;
import static by.clevertec.constants.Constants.NOT_FOUND;
import static by.clevertec.constants.Constants.NO_CONTENT;
import static by.clevertec.constants.Constants.OK;
import static by.clevertec.constants.Constants.QUERY;
import static by.clevertec.constants.Constants.RETRIEVED_THE_SEARCH_RESULTS;
import static by.clevertec.constants.Constants.SORT_BY;
import static by.clevertec.constants.Constants.SORT_ORDER;
import static by.clevertec.constants.Constants.START_INDEX;
import static by.clevertec.constants.Constants.SUCCESSFULLY_CREATED_THE_COMMENT;
import static by.clevertec.constants.Constants.SUCCESSFULLY_DELETED_THE_COMMENT;
import static by.clevertec.constants.Constants.SUCCESSFULLY_RETRIEVED_THE_COMMENT;
import static by.clevertec.constants.Constants.SUCCESSFULLY_UPDATED_THE_COMMENT;
import static by.clevertec.constants.Constants.UUID;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentsController {


    private final CommentsService commentsService;

    @Operation(
            summary = "Get a specific comment by its ID",
            description = "Retrieves a comment by its unique UUID.",
            parameters = {
                    @Parameter(name = UUID, description = "The UUID of the comment to retrieve", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = OK, description = SUCCESSFULLY_RETRIEVED_THE_COMMENT,
                            content = @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = CommentsDtoResponse.class))),
                    @ApiResponse(responseCode = NOT_FOUND, description = COMMENT_NOT_FOUND),
                    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR_REPORT)
            })
    @GetMapping("/{uuid}")
    public ResponseEntity<CommentsDtoResponse> findComment(@PathVariable UUID uuid) {
        CommentsDtoResponse response = commentsService.findById(uuid);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search comments by text and username",
            description = "Searches for comments based on a query string, text fields, and optional filters like sorting and pagination.",
            parameters = {
                    @Parameter(name = QUERY, description = "The search query for comments (used for full-text search)", required = true),
                    @Parameter(name = START_INDEX, description = "The starting index for pagination (default is 0)", in = ParameterIn.QUERY),
                    @Parameter(name = MAX_RESULTS, description = "The maximum number of results per page (default is 5)", in = ParameterIn.QUERY),
                    @Parameter(name = FIELDS, description = "The fields to search within (default is 'text')", in = ParameterIn.QUERY),
                    @Parameter(name = SORT_BY, description = "The field to sort by (default is 'sort_text')", in = ParameterIn.QUERY),
                    @Parameter(name = SORT_ORDER, description = "The order to sort results in ('ASC' or 'DESC', default is 'ASC')", in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = OK, description = RETRIEVED_THE_SEARCH_RESULTS,
                            content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = BAD_REQUEST, description = INVALID_QUERY_PARAMETERS),
                    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR_REPORT)
            })
    @GetMapping("/search")
    public ResponseEntity<List<CommentsDtoResponse>> searchCommentsByTextAndUsername(
                                                            @RequestBody String query,
                                                            @RequestParam(defaultValue = "0") int startIndex,
                                                            @RequestParam(defaultValue = "5") int maxResults,
                                                            @RequestParam(defaultValue = "text") String fields,
                                                            @RequestParam(defaultValue = "sort_text") String sortBy,
                                                            @RequestParam(defaultValue = "ASC") SortOrder sortOrder) {

        List<CommentsDtoResponse> commentsDtoResponses = commentsService.fullTextSearchByTextAndUsernameField(query, startIndex, maxResults,
                fields, sortBy, sortOrder);
        return ResponseEntity.ok(commentsDtoResponses);
    }

    @Operation(
            summary = "Create a new comment for a specific news",
            description = "Creates a new comment for the news identified by the UUID and returns the created comment's details.",
            parameters = {
                    @Parameter(name = UUID, description = "The UUID of the news to associate the comment with", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = CREATED, description = SUCCESSFULLY_CREATED_THE_COMMENT,
                            content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = BAD_REQUEST, description = INVALID_INPUT_DATA),
                    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR_REPORT)
            })
    @PostMapping("/{uuid}")
    public ResponseEntity<String> createComment(@Validated @RequestBody CommentDtoRequest commentDtoRequest,
                                                @PathVariable UUID uuid) {
        CommentsDtoResponse commentsDtoResponse = commentsService.create(uuid, commentDtoRequest);
        URI location = URI.create("/comments/" + commentsDtoResponse.getId());
        return ResponseEntity.created(location).body(SUCCESSFULLY_CREATED_THE_COMMENT);
    }

    @Operation(
            summary = "Update an existing comment",
            description = "Updates an existing comment identified by its UUID and returns the updated comment details.",
            parameters = {
                    @Parameter(name = UUID, description = "The UUID of the comment to update", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = OK, description = SUCCESSFULLY_UPDATED_THE_COMMENT,
                            content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = CommentsDtoResponse.class))),
                    @ApiResponse(responseCode = BAD_REQUEST, description = INVALID_INPUT_DATA),
                    @ApiResponse(responseCode = NOT_FOUND, description = COMMENT_NOT_FOUND),
                    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR_REPORT)
            })
    @PutMapping("/{uuid}")
    public ResponseEntity<CommentsDtoResponse> updateComment(@PathVariable UUID uuid,
                                                             @RequestBody CommentDtoRequestUpdate commentDtoRequestUpdate) {
        CommentsDtoResponse commentsDtoResponse = commentsService.update(uuid, commentDtoRequestUpdate);
        return ResponseEntity.ok(commentsDtoResponse);
    }

    @Operation(
            summary = "Delete a comment",
            description = "Deletes a comment identified by its UUID.",
            parameters = {
                    @Parameter(name = UUID, description = "The UUID of the comment to delete", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = NO_CONTENT, description = SUCCESSFULLY_DELETED_THE_COMMENT),
                    @ApiResponse(responseCode = NOT_FOUND, description = COMMENT_NOT_FOUND),
                    @ApiResponse(responseCode = INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR_REPORT)
            })
    @DeleteMapping("/{uuid}")

    public ResponseEntity<String> deleteComment(@PathVariable UUID uuid) {
        commentsService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
