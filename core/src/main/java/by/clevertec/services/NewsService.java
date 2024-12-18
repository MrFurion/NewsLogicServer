package by.clevertec.services;

import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.models.News;

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
     * @return News
     */
    News findById(UUID id);

    /**
     * Create new news with use data of NewsDtoRequest.
     *
     * @param newsDtoRequest data of newsDtoRequest
     * @return NewsDtoResponse
     */
    NewsDtoResponse create(NewsDtoRequest newsDtoRequest);

    /**
     * Update news by her id with use NewsDtoRequest
     *
     * @param newsDtoRequest data of newsDataRequest
     * @param uuid id of news
     * @return update news with use NewsDtoResponse
     */
    NewsDtoResponse update(NewsDtoRequest newsDtoRequest, UUID uuid);

    /**
     * Delete news by her id.
     *
     * @param uuid id of news
     */
    void delete(UUID uuid);
}
