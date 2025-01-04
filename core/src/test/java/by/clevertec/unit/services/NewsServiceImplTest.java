package by.clevertec.unit.services;

import by.clevertec.data.CreateData;
import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.exception.NewsNotFoundException;
import by.clevertec.lucene.repository.NewsLuceneRepository;
import by.clevertec.mapper.NewsMapper;
import by.clevertec.models.Comment;
import by.clevertec.models.News;
import by.clevertec.repositories.NewsRepository;
import by.clevertec.services.impl.NewsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static by.clevertec.constants.Constants.PAGE;
import static by.clevertec.constants.Constants.PAGE_SIZE;
import static by.clevertec.constants.Constants.SEARCHABLE_FIELDS;
import static by.clevertec.constants.Constants.SEARCH_ELEMENT;
import static by.clevertec.constants.Constants.SORT_FIELD;
import static by.clevertec.constants.Constants.SORT_ORDER;
import static org.hibernate.search.util.common.impl.Contracts.assertNotNull;
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

    @Mock
    private NewsLuceneRepository newsLuceneRepository;

    @InjectMocks
    private NewsServiceImpl newsServiceImpl;

    @Test
    void findById_WhenIdExists() {

        //given
        News news = CreateData.createNews();
        UUID id = news.getId();
        NewsDtoResponse expectNewsDtoResponse = CreateData.createNewsDtoResponse();

        //when
        when(newsRepository.findById(id)).thenReturn(Optional.of(news));
        when(newsMapper.toNewsDtoResponse(news)).thenReturn(expectNewsDtoResponse);

        NewsDtoResponse actualNewsDtoResponse = newsServiceImpl.findById(id);

        //then
        assertEquals(expectNewsDtoResponse, actualNewsDtoResponse);
    }

    @Test
    void findById_WhenIdDoesNotExist() {

        //given
        UUID id = CreateData.createRandomUUID();

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
        UUID id = CreateData.createRandomUUID();
        NewsDtoRequestUpdate newsDtoRequestUpdate = CreateData.updateDtoRequestNews();

        //when
        when(newsRepository.findById(id)).thenReturn(Optional.empty());

        //then
        assertThrows(NewsNotFoundException.class, () -> newsServiceImpl.update(newsDtoRequestUpdate, id));
    }

    @Test
    void delete_WhenIdExists() {

        //given
        UUID existingId = CreateData.createRandomUUID();
        int deleteOperationsCountExpect = 1;

        //when
        when(newsRepository.deleteIfExists(existingId)).thenReturn(deleteOperationsCountExpect);

        newsServiceImpl.delete(existingId);

        //then
        verify(newsRepository).deleteIfExists(existingId);
    }

    @Test
    void delete_WhenIdDoesNotExist() {

        //given
        UUID nonExistentId = CreateData.createRandomUUID();
        int deleteOperationsCountExpect = 0;

        //when
        when(newsRepository.deleteIfExists(nonExistentId)).thenReturn(deleteOperationsCountExpect);

        //then
        assertThrows(NewsNotFoundException.class, () -> newsServiceImpl.delete(nonExistentId));
        verify(newsRepository).deleteIfExists(nonExistentId);
    }

    @Test
    void findAll_shouldReturnMappedPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        News news = CreateData.createNews();
        NewsDtoResponse dtoResponse = CreateData.createNewsDtoResponse();
        Page<News> newsPage = new PageImpl<>(List.of(news), pageable, 1);
        Page<NewsDtoResponse> expectedPage = new PageImpl<>(List.of(dtoResponse), pageable, 1);

        //when
        Mockito.when(newsRepository.findAll(pageable)).thenReturn(newsPage);
        Mockito.when(newsMapper.toNewsDtoResponse(news)).thenReturn(dtoResponse);
        Page<NewsDtoResponse> result = newsServiceImpl.findAll(pageable);

        //then
        assertNotNull(result, "Result should not be null");
        assertEquals(expectedPage.getContent(), result.getContent());
        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        verify(newsRepository).findAll(pageable);
        verify(newsMapper).toNewsDtoResponse(news);
    }

    @Test
    void findByIdWithAllComments_shouldReturnNewsWithPaginatedComments() {
        //given
        UUID uuid = UUID.randomUUID();
        int page = 0;
        int size = 2;

        Comment comment1 = CreateData.createComment();
        Comment comment2 = CreateData.createComment();
        Comment comment3 = CreateData.createComment();

        News news = CreateData.createNews();
        news.getComments().add(comment1);
        news.getComments().add(comment2);
        news.getComments().add(comment3);

        NewsDtoResponse newsDtoResponse = CreateData.createNewsDtoResponse();

        //when
        Mockito.when(newsRepository.findById(uuid)).thenReturn(Optional.of(news));
        Mockito.when(newsMapper.toNewsDtoResponse(news)).thenReturn(newsDtoResponse);
        Page<NewsDtoResponse> result = newsServiceImpl.findByIdWithAllComments(uuid, page, size);

        //then
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        NewsDtoResponse resultDto = result.getContent().get(0);
        assertNotNull(resultDto.getComments(), "Comments should not be null");
        assertEquals(2, resultDto.getComments().size());
        assertEquals(comment1.getText(), resultDto.getComments().get(0).getText());
        assertEquals(comment2.getText(), resultDto.getComments().get(1).getText());
        Mockito.verify(newsRepository, Mockito.times(1)).findById(uuid);
        Mockito.verify(newsMapper, Mockito.times(1)).toNewsDtoResponse(news);
    }

    @Test
    void fullTextSearchByTitleAndTextField_shouldReturnMappedDtoList() {
        // Arrange
        News news1 = CreateData.createNews();
        News news2 = CreateData.updateNews();

        List<News> mockNewsList = List.of(news1, news2);

        NewsDtoResponse dto1 = CreateData.createNewsDtoResponse();
        NewsDtoResponse dto2 = NewsDtoResponse.builder()
                .id(news2.getId())
                .title(news2.getTitle())
                .text(news2.getText())
                .build();

        List<NewsDtoResponse> expectedDtoList = List.of(dto1, dto2);

        //when
        Mockito.when(newsLuceneRepository.fullTextSearch(
                        SEARCH_ELEMENT, PAGE, PAGE_SIZE, List.of(SEARCHABLE_FIELDS), SORT_FIELD, SORT_ORDER))
                .thenReturn(mockNewsList);

        Mockito.when(newsMapper.toNewsDtoResponseList(mockNewsList)).thenReturn(expectedDtoList);

        List<NewsDtoResponse> result = newsServiceImpl.fullTextSearchByTitleAndTextField(
                SEARCH_ELEMENT, PAGE, PAGE_SIZE, SEARCHABLE_FIELDS, SORT_FIELD, SORT_ORDER);

        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(expectedDtoList.size(), result.size());
        assertEquals(expectedDtoList, result);
        verify(newsLuceneRepository).fullTextSearch(
                SEARCH_ELEMENT, PAGE, PAGE_SIZE, List.of(SEARCHABLE_FIELDS), SORT_FIELD, SORT_ORDER);
        verify(newsMapper).toNewsDtoResponseList(mockNewsList);
    }
}
