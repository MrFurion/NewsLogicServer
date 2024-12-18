package by.clevertec.services.impl;

import by.clevertec.dto.request.NewsDtoRequest;
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
@Transactional(readOnly=true)
@Slf4j
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    @Override
    public News findById(UUID id) {
        return newsRepository.findById(id).orElseThrow(NewsNotFoundException::new);
    }

    @Override
    @Transactional
    public NewsDtoResponse create(NewsDtoRequest newsDtoRequest) {
        News news = newsMapper.toNews(newsDtoRequest);
        news.setTime(Instant.now());
        newsRepository.save(news);
        return newsMapper.toNewsDtoResponse(news);
    }

    @Override
    @Transactional
    public NewsDtoResponse update(NewsDtoRequest
                                  newsDtoRequest, UUID uuid) {

        Optional<News> newsOptional = newsRepository.findById(uuid);
        if(newsOptional.isPresent()) {
            News news = newsOptional.get();
            Optional.ofNullable(newsDtoRequest.getTitle()).ifPresent(news::setTitle);
            Optional.ofNullable(newsDtoRequest.getText()).ifPresent(news::setText);
            newsRepository.save(news);
            return newsMapper.toNewsDtoResponse(news);
        } else {
            log.error("News not found " + uuid);
            throw new NewsNotFoundException();
        }
    }

    @Override
    @Transactional
    public void delete(UUID uuid) {
        if(!newsRepository.existsById(uuid)) {
            log.error("News not found " + uuid);
            throw new NewsNotFoundException();
        }
        newsRepository.deleteById(uuid);
    }
}
