package by.clevertec.services.impl;


import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.exceptions.NewsNotFoundException;
import by.clevertec.exceptions.UserNameNotFoundException;
import by.clevertec.lucene.repository.NewsLuceneRepository;
import by.clevertec.mapper.NewsMapper;
import by.clevertec.models.Comment;
import by.clevertec.models.News;
import by.clevertec.repositories.NewsRepository;
import by.clevertec.services.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static by.clevertec.util.SecurityContext.getUserNameFromContext;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private static final String NEWS_NOT_FOUND = "News not found ";
    private static final String CACHE_NAME_FOR_NEWS = "news";

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;
    private final NewsLuceneRepository newsLuceneRepository;

    @Override
    @Cacheable(cacheNames = CACHE_NAME_FOR_NEWS, key = "#id")
    public NewsDtoResponse findById(UUID id) {
        News news = newsRepository.findById(id).orElseThrow(NewsNotFoundException::new);
        return newsMapper.toNewsDtoResponse(news);
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME_FOR_NEWS, key = "#pageable")
    public Page<NewsDtoResponse> findAll(Pageable pageable) {
        Page<News> newsPage = newsRepository.findAll(pageable);
        return newsPage.map(newsMapper::toNewsDtoResponse);
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME_FOR_NEWS, key = "#page + '_' + #size")
    public Page<NewsDtoResponse> findByIdWithAllComments(UUID uuid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        News news = newsRepository.findById(uuid).orElseThrow(NewsNotFoundException::new);

        NewsDtoResponse newsDtoResponse = newsMapper.toNewsDtoResponse(news);

        List<Comment> paginatedComments = news.getComments().stream()
                .skip((long) page * size)
                .limit(size)
                .toList();

        newsDtoResponse.setComments(paginatedComments);

        return new PageImpl<>(Collections.singletonList(newsDtoResponse), pageable, 1);
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME_FOR_NEWS, key = "T(String).format('%s-%d-%d-%s-%s-%s', " +
                                                       "#searchElement, #pageStart, #pageSize, " +
                                                       "#searchableFields, #sortField, #sortOrder)")
    public List<NewsDtoResponse> fullTextSearchByTitleAndTextField(String searchElement, int pageStart, int pageSize,
                                                                   String searchableFields, String sortField, SortOrder sortOrder) {
        List<News> news = newsLuceneRepository
                .fullTextSearch(searchElement, pageStart, pageSize, List.of(searchableFields), sortField, sortOrder);
        return newsMapper.toNewsDtoResponseList(news);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CACHE_NAME_FOR_NEWS, key = "#result.id")
    public NewsDtoResponse create(NewsDtoRequest newsDtoRequest) {
        News news = newsMapper.toNews(newsDtoRequest);
        news.setTime(Instant.now());
        String userName = getUserNameFromContext();
        Set<String> names = new HashSet<>();
        names.add(userName);
        news.setUserName(names);
        newsRepository.save(news);
        log.info("News created successfully at time: {}", news.getTime());
        return newsMapper.toNewsDtoResponse(news);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CACHE_NAME_FOR_NEWS, key = "#uuid")
    public NewsDtoResponse update(NewsDtoRequestUpdate newsDtoRequestUpdate, UUID uuid) {
        News news = newsRepository.findById(uuid)
            .orElseThrow(() -> {
                log.error(NEWS_NOT_FOUND, uuid);
                return new NewsNotFoundException();
            });

        Optional.ofNullable(newsDtoRequestUpdate.getTitle()).ifPresent(news::setTitle);
        Optional.ofNullable(newsDtoRequestUpdate.getText()).ifPresent(news::setText);

        if (!news.getUserName().contains(getUserNameFromContext())) {
            throw new UserNameNotFoundException();
        }

        News updatedNews = newsRepository.save(news);
        log.info("News updated successfully with id {} at time: {}", updatedNews.getId(), news.getTime());
        return newsMapper.toNewsDtoResponse(news);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME_FOR_NEWS, key = "#uuid")
    public void delete(UUID uuid) {
        String username = getUserNameFromContext();
        int deletedCount = newsRepository.deleteIfExists(uuid, username);

        if (deletedCount == 0) {
            log.error(NEWS_NOT_FOUND, uuid);
            throw new NewsNotFoundException();
        }
        log.info("News deleted successfully with id {}", uuid);
    }
}
