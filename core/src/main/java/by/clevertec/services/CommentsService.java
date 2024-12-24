package by.clevertec.services;

import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.request.CommentDtoRequestUpdate;
import by.clevertec.dto.response.CommentsDtoResponse;

import java.util.UUID;

/**
 * The CommentsService interface defines the contract for services related to comments operations.
 * This includes methods for retrieving, creating, updating, and deleting comments.
 * The implementation of this interface is expected to provide the actual business logic
 * for handling comments data in the application.
 */
public interface CommentsService {

    /**
     * Find comment by its identifier.
     * Allows retrieving a comment item by its ID from the comment repository,
     * ensuring safe handling in case the comment item with the specified ID is not present.
     *
     * @param uuid Comment ID
     * @return CommentsDtoResponse
     */
    CommentsDtoResponse findById(UUID uuid);


    /**
     * Create new comment with use data of CommentDtoRequest and News uuid.
     *
     * @param newsUuid          data of news uuid
     * @param commentDtoRequest data of newsDtoRequest
     * @return CommentDtoResponse
     */
    CommentsDtoResponse create(UUID newsUuid, CommentDtoRequest commentDtoRequest);

    /**
     * Update comments by its id with use CommentDtoRequestUpdate
     *
     * @param commentDtoRequestUpdate data of commentDataRequestUpdate
     * @param uuid                    id of comment
     * @return update comment with use commentDtoResponse
     */
    CommentsDtoResponse update(UUID uuid, CommentDtoRequestUpdate commentDtoRequestUpdate);

    /**
     * Delete comment by its id.
     *
     * @param uuid id of comment
     */
    void delete(UUID uuid);
}
