package by.clevertec.services.impl;

import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.exception.NewsNotFoundException;
import by.clevertec.lucene.repository.NewsLuceneRepository;
import by.clevertec.mapper.NewsMapper;
import by.clevertec.models.Comment;
import by.clevertec.models.News;
import by.clevertec.repositories.NewsRepository;
import by.clevertec.services.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;
    private final NewsLuceneRepository newsLuceneRepository;

    @Override
    public NewsDtoResponse findById(UUID id) {
        News news = newsRepository.findById(id).orElseThrow(NewsNotFoundException::new);
        return newsMapper.toNewsDtoResponse(news);
    }

    public Page<NewsDtoResponse> findAll(Pageable pageable) {
        Page<News> newsPage = newsRepository.findAll(pageable);
        return newsPage.map(newsMapper::toNewsDtoResponse);
    }

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

    public List<NewsDtoResponse> fullTextSearchByTitleAndTextField(String searchElement,
                                            int page,
                                            int pageSize,
                                            String searchableFields,
                                            String sortField,
                                            SortOrder sortOrder
                                            ) {

        List<News> news = newsLuceneRepository
                .fullTextSearch(searchElement, page, pageSize, List.of(searchableFields), sortField, sortOrder);
        return newsMapper.toNewsDtoResponseList(news);
    }

    @Override
    @Transactional
    public NewsDtoResponse create(NewsDtoRequest newsDtoRequest) {
        News news = newsMapper.toNews(newsDtoRequest);
        news.setTime(Instant.now());
        newsRepository.save(news);
        log.info("News created successfully at time: {}", news.getTime());
        return newsMapper.toNewsDtoResponse(news);
    }

    @Override
    @Transactional
    public NewsDtoResponse update(NewsDtoRequestUpdate
                                          newsDtoRequestUpdate, UUID uuid) {

        Optional<News> newsOptional = newsRepository.findById(uuid);
        if (newsOptional.isPresent()) {
            Optional.ofNullable(newsDtoRequestUpdate.getTitle()).ifPresent(newsOptional.get()::setTitle);
            Optional.ofNullable(newsDtoRequestUpdate.getText()).ifPresent(newsOptional.get()::setText);
            News newsUpdate = newsRepository.save(newsOptional.get());
            log.info("News updated successfully with id {} at time: {}", newsUpdate.getId(),
                    newsOptional.get().getTime());
            return newsMapper.toNewsDtoResponse(newsOptional.get());
        } else {
            log.error("News not found " + uuid);
            throw new NewsNotFoundException();
        }
    }

    @Override
    @Transactional
    public void delete(UUID uuid) {
        int deletedCount = newsRepository.deleteIfExists(uuid);

        if (deletedCount == 0) {
            log.error("News not found with id {}", uuid);
            throw new NewsNotFoundException();
        }
        log.info("News deleted successfully with id {}", uuid);
    }
}
