package by.clevertec.services.impl;

import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.exception.NewsNotFoundException;
import by.clevertec.mapper.NewsMapper;
import by.clevertec.models.News;
import by.clevertec.repositories.NewsRepository;
import by.clevertec.services.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    @Override
    public NewsDtoResponse findById(UUID id) {
        News news = newsRepository.findById(id).orElseThrow(NewsNotFoundException::new);
        return newsMapper.toNewsDtoResponse(news);
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
