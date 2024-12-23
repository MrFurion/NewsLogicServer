package by.clevertec.services.impl;

import by.clevertec.data.CreateData;
import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.exception.NewsNotFoundException;
import by.clevertec.mapper.NewsMapper;
import by.clevertec.models.News;
import by.clevertec.repositories.NewsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private NewsMapper newsMapper;

    @InjectMocks
    private NewsServiceImpl newsServiceImpl;

    @Test
    void findById_WhenIdExists() {
        //given
        News expectNews = CreateData.createNews();
        UUID id = expectNews.getId();

        //when
        when(newsRepository.findById(id)).thenReturn(Optional.of(expectNews));

        News actualNews = newsServiceImpl.findById(id);

        //then
        assertEquals(expectNews, actualNews);
    }

    @Test
    void findById_WhenIdDoesNotExist() {
        //given
        UUID id = CreateData.createUUID();

        //when
        when(newsRepository.findById(id)).thenReturn(Optional.empty());

        //then
        assertThrows(NewsNotFoundException.class, () -> newsServiceImpl.findById(id));
    }

    @Test
    void create() {
        //given
        News expectNews = CreateData.createNews();
        NewsDtoRequest newsDtoRequest = CreateData.createNewsDtoRequest();
        NewsDtoResponse expectedResponse = CreateData.createNewsDtoResponse();

        //when
        when(newsMapper.toNews(newsDtoRequest)).thenReturn(expectNews);
        when(newsMapper.toNewsDtoResponse(expectNews)).thenReturn(expectedResponse);

        when(newsRepository.save(expectNews)).thenReturn(expectNews);

        NewsDtoResponse actualResponse = newsServiceImpl.create(newsDtoRequest);

        //then
        assertEquals(expectedResponse.getText(), actualResponse.getText());
        assertEquals(expectedResponse.getTitle(), actualResponse.getTitle());
        verify(newsRepository, times(1)).save(any());
    }

    @Test
    void update_WhenIdExists() {
        //given

        News expectNews = CreateData.createNews();
        News updateNews = CreateData.updateNews();
        UUID id = expectNews.getId();
        updateNews.setId(id);
        NewsDtoRequestUpdate newsDtoRequestUpdate = CreateData.updateDtoRequestNews();
        NewsDtoResponse expectResponse = CreateData.createNewsDtoResponse();

        //when
        when(newsRepository.findById(id)).thenReturn(Optional.of(expectNews));
        when(newsRepository.save(expectNews)).thenReturn(updateNews);
        when(newsMapper.toNewsDtoResponse(any(News.class))).thenReturn(expectResponse);

        NewsDtoResponse actualResponse = newsServiceImpl.update(newsDtoRequestUpdate, id);

        //then
        assertEquals(expectResponse.getTitle(), actualResponse.getTitle());
    }

    @Test
    void update_WhenIdDoesNotExist() {

        //given
        UUID id = CreateData.createUUID();

        //when
        when(newsRepository.findById(id)).thenReturn(Optional.empty());

        //then
        assertThrows(NewsNotFoundException.class, () -> newsServiceImpl.findById(id));

    }

    @Test
    void delete_WhenIdExists() {
        //given
        UUID existingId = CreateData.createUUID();
        int deleteOperationsCountExpect = 1;

        //when
        when(newsRepository.deleteIfExists(existingId)).thenReturn(deleteOperationsCountExpect);

        newsServiceImpl.delete(existingId);

        //then
        verify(newsRepository, times(deleteOperationsCountExpect)).deleteIfExists(existingId);
    }

    @Test
    void delete_WhenIdDoesNotExist() {
        //given
        UUID nonExistentId = CreateData.createUUID();
        int deleteOperationsCountExpect = 0;

        //when
        when(newsRepository.deleteIfExists(nonExistentId)).thenReturn(deleteOperationsCountExpect);

        //then
        assertThrows(NewsNotFoundException.class, () -> newsServiceImpl.delete(nonExistentId));
        verify(newsRepository, times(1)).deleteIfExists(nonExistentId);

    }
}