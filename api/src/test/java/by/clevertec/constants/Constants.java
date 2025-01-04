package by.clevertec.constants;

import org.hibernate.search.engine.search.sort.dsl.SortOrder;

public final class Constants {

    private Constants() {
    }

    public static final String NEWS_ID = "/news/{id}";
    public static final String NEWS_CREATED_SUCCESSFULLY = "News created successfully";
    public static final String NEWS = "/news";
    public static final String COMMENTS_UUID = "/comments/{uuid}";
    public static final String COMMENT_CREATED_SUCCESSFULLY = "Comment created successfully";
    public static final int PAGE = 0;
    public static final int SIZE = 5;
    public static final String UPDATED_TITLE = "Updated Title";
    public static final String UPDATED_TEXT_OF_THE_NEWS = "Updated text of the news.";
    public static final String QUERY = "Sample Search";
    public static final int START_INDEX = 0;
    public static final int MAX_RESULTS = 5;
    public static final String FIELDS = "title";
    public static final String SORT_BY = "sort_title";
    public static final SortOrder SORT_ORDER = SortOrder.ASC;
}
