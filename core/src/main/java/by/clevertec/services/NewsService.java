package by.clevertec.services;

import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * NewsService interface defines the contract for services related to news operations.
 * This includes methods for retrieving, creating, updating, and deleting news items.
 * The implementation of this interface is expected to provide the actual business logic
 * for handling news data in the application.
 */

public interface NewsService {

    /**
     * Find news by its identifier.
     * Allows retrieving a news item by its ID from the news repository,
     * ensuring safe handling in case the news item with the specified ID is not present.
     *
     * @param id News ID
     * @return NewsDtoResponse
     */
    NewsDtoResponse findById(UUID id);

    /**
     * Returns a page of {@code News} objects based on the specified pagination and sorting parameters.
     *
     * @param pageable an object containing pagination and sorting information
     * @return a {@link Page} object
     */
    Page<NewsDtoResponse> findAll(Pageable pageable);

    /**
     * Retrieves a paginated list of {@code NewsDtoResponse} objects, including all comments
     * associated with the news record identified by the specified UUID.
     *
     * @param uuid the unique identifier of the news record for which comments should be retrieved.
     * @param page the page number of the results (starting from 0). Used for pagination.
     * @param size the number of items to include per page. Used to limit the size of the result set.
     * @return a {@code Page<NewsDtoResponse>} object containing the requested news data with all associated comments,
     * and pagination details such as total elements and total pages.
     */
    Page<NewsDtoResponse> findByIdWithAllComments(UUID uuid, int page, int size);

    /**
     * Performs a full-text search in the Lucene index for the specified search element.
     *
     * @param searchElement    the string to search for; a keyword or phrase that will
     *                         be looked up in the specified fields.
     * @param page             the page number of the results (starting from 0).
     *                         Used for pagination.
     * @param pageSize         the number of items per page.
     *                         Used to limit the number of returned results.
     * @param searchableFields the field or list of fields to search within.
     *                         Fields must be pre-configured as indexable in Lucene
     *                         (e.g., "title", "content").
     * @param sortField        the field by which the results will be sorted.
     *                         The field must be pre-configured as sortable in Lucene.
     * @param sortOrder        the sort direction: {@code SortOrder.ASC} for ascending
     *                         order, {@code SortOrder.DESC} for descending order.
     * @return a list of {@code NewsDtoResponse} objects representing the search results.
     * Each object contains information about a record matching the search query.
     */
    List<NewsDtoResponse> fullTextSearchByTitleAndTextField(String searchElement,
                                                            int page,
                                                            int pageSize,
                                                            String searchableFields,
                                                            String sortField,
                                                            SortOrder sortOrder
    );

    /**
     * Create new news with use data of NewsDtoRequest.
     *
     * @param newsDtoRequest data of newsDtoRequest
     * @return NewsDtoResponse
     */
    NewsDtoResponse create(NewsDtoRequest newsDtoRequest);

    /**
     * Update news by its id with use NewsDtoRequestUpdate
     *
     * @param newsDtoRequestUpdate data of newsDataRequestUpdate
     * @param uuid                 id of news
     * @return update news with use NewsDtoResponse
     */
    NewsDtoResponse update(NewsDtoRequestUpdate newsDtoRequestUpdate, UUID uuid);

    /**
     * Delete news by its id.
     *
     * @param uuid id of news
     */
    void delete(UUID uuid);
}
