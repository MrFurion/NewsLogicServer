package by.clevertec.services.impl;

import by.clevertec.exception.NewsNotFoundException;
import by.clevertec.models.News;
import by.clevertec.repositories.NewsRepository;
import by.clevertec.services.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly=true)
@Slf4j
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository repository;

    @Override
    public News findById(UUID id) {
        return repository.findById(id).orElseThrow(NewsNotFoundException::new);
    }

    @Override
    public void create() {

    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }

    private final NewsRepository newsRepository;
}
