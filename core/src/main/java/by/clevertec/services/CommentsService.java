package by.clevertec.services;

import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.request.CommentDtoRequestUpdate;
import by.clevertec.dto.response.CommentsDtoResponse;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;

import java.util.List;
import java.util.UUID;

/**
 * The CommentsService interface defines the contract for services related to comments operations.
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
     * Executes a full-text search in the Lucene index for comments based on the given search element.
     *
     * @param searchElement    the text to search for; a keyword or phrase to be matched in the specified fields.
     * @param page             the page number of the results (starting from 0). Used for pagination.
     * @param pageSize         the number of items to include per page. Used to limit the size of the result set.
     * @param searchableFields the field or list of fields in which the search will be conducted.
     *                         Fields must be pre-configured as searchable in Lucene (e.g., "content", "authorName").
     * @param sortField        the field by which the results will be sorted.
     *                         The field must be pre-configured as sortable in Lucene.
     * @param sortOrder        the sort direction: {@code SortOrder.ASC} for ascending order or
     *                         {@code SortOrder.DESC} for descending order.
     * @return a list of {@code CommentsDtoResponse} objects representing the search results.
     *         Each object contains information about a comment that matches the search criteria.
     */
    List<CommentsDtoResponse> fullTextSearchByTextAndUsernameField(String searchElement, int page, int pageSize,
                                                                   String searchableFields, String sortField,
                                                                   SortOrder sortOrder);

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
