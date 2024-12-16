package by.clevertec.services;

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
    void create();
    void update();
    void delete();
}
