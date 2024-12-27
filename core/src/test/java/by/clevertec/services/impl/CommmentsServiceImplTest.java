package by.clevertec.services.impl;

import by.clevertec.data.CreateData;
import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.request.CommentDtoRequestUpdate;
import by.clevertec.dto.response.CommentsDtoResponse;
import by.clevertec.exception.CommentNotFoundException;
import by.clevertec.mapper.CommentsMapper;
import by.clevertec.models.Comment;
import by.clevertec.models.News;
import by.clevertec.repositories.CommentsRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommmentsServiceImplTest {

    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private CommentsMapper commentsMapper;

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private CommentsServiceImpl commentsServiceImpl;

    @Test
    void findById_WhenIdExists() {

        //given
        Comment comment = CreateData.createComment();
        UUID id = comment.getId();
        CommentsDtoResponse expectCommentDtoResponse = CreateData.createCommentsDtoResponse();

        //when
        when(commentsRepository.findById(id)).thenReturn(Optional.of(comment));
        when(commentsMapper.toCommentsDtoResponse(comment)).thenReturn(expectCommentDtoResponse);

        CommentsDtoResponse actualNewsDtoResponse = commentsServiceImpl.findById(id);

        //then
        assertEquals(expectCommentDtoResponse, actualNewsDtoResponse);
    }

    @Test
    void findById_WhenIdDoesNotExist() {

        //given
        UUID id = CreateData.createRandomUUID();

        //when
        when(commentsRepository.findById(id)).thenReturn(Optional.empty());

        //then
        assertThrows(CommentNotFoundException.class, () -> commentsServiceImpl.findById(id));
    }

    @Test
    void create() {

        //given
        Comment expectedComment = CreateData.createComment();
        CommentDtoRequest commentDtoRequest = CreateData.createCommentDtoRequest();
        CommentsDtoResponse expectedCommentDtoResponse = CreateData.createCommentsDtoResponse();
        News expectedNews = CreateData.createNews();
        UUID newsUuid = CreateData.createRandomUUID();

        //when
        when(commentsMapper.toComment(commentDtoRequest)).thenReturn(expectedComment);
        when(commentsMapper.toCommentsDtoResponse(expectedComment)).thenReturn(expectedCommentDtoResponse);
        when(newsRepository.findById(newsUuid)).thenReturn(Optional.of(expectedNews));
        when(commentsRepository.save(expectedComment)).thenReturn(expectedComment);

        CommentsDtoResponse actualResponse = commentsServiceImpl.create(newsUuid, commentDtoRequest);

        //then
        assertEquals(expectedCommentDtoResponse.getText(), actualResponse.getText());
        assertEquals(expectedCommentDtoResponse.getUsername(), actualResponse.getUsername());
        verify(commentsRepository).save(any());
        verify(newsRepository).findById(newsUuid);
    }

    @Test
    void update_WhenIdExists() {

        //given
        Comment expectedComment = CreateData.createComment();
        Comment updateComment = CreateData.updateComment();

        UUID id = expectedComment.getId();
        updateComment.setId(id);

        CommentDtoRequestUpdate commentDtoRequestUpdate = CreateData.updateDtoRequestComment();
        CommentsDtoResponse expectResponse = CreateData.createCommentsDtoResponse();

        //when
        when(commentsRepository.findById(id)).thenReturn(Optional.of(expectedComment));
        when(commentsRepository.save(expectedComment)).thenReturn(updateComment);
        when(commentsMapper.toCommentsDtoResponse(any(Comment.class))).thenReturn(expectResponse);

        CommentsDtoResponse actualResponse = commentsServiceImpl.update(id, commentDtoRequestUpdate);

        //then
        assertEquals(expectResponse.getText(), actualResponse.getText());
    }

    @Test
    void update_WhenIdDoesNotExist() {

        //given
        UUID id = CreateData.createRandomUUID();

        //when
        when(commentsRepository.findById(id)).thenReturn(Optional.empty());

        //then
        assertThrows(CommentNotFoundException.class, () -> commentsServiceImpl.update(id, CreateData.updateDtoRequestComment()));
    }

    @Test
    void delete_WhenIdExists() {

        //given
        UUID existingId = CreateData.createRandomUUID();
        int deleteOperationsCountExpect = 1;

        //when
        when(commentsRepository.deleteIfExists(existingId)).thenReturn(deleteOperationsCountExpect);

        commentsServiceImpl.delete(existingId);

        //then
        verify(commentsRepository).deleteIfExists(existingId);
    }

    @Test
    void delete_WhenIdDoesNotExist() {

        //given
        UUID nonExistentId = CreateData.createRandomUUID();
        int deleteOperationsCountExpect = 0;

        //when
        when(commentsRepository.deleteIfExists(nonExistentId)).thenReturn(deleteOperationsCountExpect);

        //then
        assertThrows(CommentNotFoundException.class, () -> commentsServiceImpl.delete(nonExistentId));
        verify(commentsRepository).deleteIfExists(nonExistentId);
    }
}
