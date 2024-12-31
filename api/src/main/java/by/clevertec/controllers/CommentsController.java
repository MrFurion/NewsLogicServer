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

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentsController {

    private final CommentsService commentsService;

    @Operation(
            summary = "Get a specific comment by its ID",
            description = "Retrieves a comment by its unique UUID.",
            parameters = {
                    @Parameter(name = "uuid", description = "The UUID of the comment to retrieve", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the comment",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CommentsDtoResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Comment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
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
                    @Parameter(name = "query", description = "The search query for comments (used for full-text search)", required = true),
                    @Parameter(name = "startIndex", description = "The starting index for pagination (default is 0)", in = ParameterIn.QUERY),
                    @Parameter(name = "maxResults", description = "The maximum number of results per page (default is 5)", in = ParameterIn.QUERY),
                    @Parameter(name = "fields", description = "The fields to search within (default is 'title')", in = ParameterIn.QUERY),
                    @Parameter(name = "sortBy", description = "The field to sort by (default is 'sort_title')", in = ParameterIn.QUERY),
                    @Parameter(name = "sortOrder", description = "The order to sort results in ('ASC' or 'DESC', default is 'ASC')", in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the search results",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @GetMapping("/search")
    public ResponseEntity<List<CommentsDtoResponse>> searchCommentsByTextAndUsername(
                                                            @RequestBody String query,
                                                            @RequestParam(defaultValue = "0") int startIndex,
                                                            @RequestParam(defaultValue = "5") int maxResults,
                                                            @RequestParam(defaultValue = "title") String fields,
                                                            @RequestParam(defaultValue = "sort_title") String sortBy,
                                                            @RequestParam(defaultValue = "ASC") SortOrder sortOrder) {

        List<CommentsDtoResponse> commentsDtoResponses = commentsService.fullTextSearchByTextAndUsernameField(query, startIndex, maxResults,
                fields, sortBy, sortOrder);
        return ResponseEntity.ok(commentsDtoResponses);
    }

    @Operation(
            summary = "Create a new comment for a specific news",
            description = "Creates a new comment for the news identified by the UUID and returns the created comment's details.",
            parameters = {
                    @Parameter(name = "uuid", description = "The UUID of the news to associate the comment with", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created the comment",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @PostMapping("/{uuid}")
    public ResponseEntity<String> createComment(@Validated @RequestBody CommentDtoRequest commentDtoRequest,
                                                @PathVariable UUID uuid) {
        CommentsDtoResponse commentsDtoResponse = commentsService.create(uuid, commentDtoRequest);
        URI location = URI.create("/comments/" + commentsDtoResponse.getId());
        return ResponseEntity.created(location).body("Comment created successfully");
    }

    @Operation(
            summary = "Update an existing comment",
            description = "Updates an existing comment identified by its UUID and returns the updated comment details.",
            parameters = {
                    @Parameter(name = "uuid", description = "The UUID of the comment to update", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated the comment",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentsDtoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "404", description = "Comment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
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
                    @Parameter(name = "uuid", description = "The UUID of the comment to delete", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the comment"),
                    @ApiResponse(responseCode = "404", description = "Comment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> deleteComment(@PathVariable UUID uuid) {
        commentsService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
