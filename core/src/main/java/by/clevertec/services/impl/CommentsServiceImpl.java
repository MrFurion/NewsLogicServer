package by.clevertec.services.impl;

import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.request.CommentDtoRequestUpdate;
import by.clevertec.dto.response.CommentsDtoResponse;

import by.clevertec.exceptions.CommentNotFoundException;
import by.clevertec.exceptions.NewsNotFoundException;
import by.clevertec.exceptions.UserNameNotFoundException;
import by.clevertec.lucene.repository.CommentsLuceneRepository;
import by.clevertec.mapper.CommentsMapper;
import by.clevertec.models.Comment;
import by.clevertec.models.News;
import by.clevertec.repositories.CommentsRepository;
import by.clevertec.repositories.NewsRepository;
import by.clevertec.services.CommentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static by.clevertec.constants.CoreConstants.COMMENT_NOT_FOUND;
import static by.clevertec.util.SecurityContext.getUserNameFromContext;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentsServiceImpl implements CommentsService {

    public static final String CACHE_NAME_FOR_COMMENTS = "comments";
    public static final String KAY_FOR_CACHE_COMMENTS = "#uuid";
    private final CommentsRepository commentsRepository;
    private final CommentsMapper commentsMapper;
    private final NewsRepository newsRepository;
    private final CommentsLuceneRepository commentsLuceneRepository;

    @Override
    @Cacheable(cacheNames = CACHE_NAME_FOR_COMMENTS, key = KAY_FOR_CACHE_COMMENTS)
    public CommentsDtoResponse findById(UUID uuid) {
        Comment comment = commentsRepository.findById(uuid).orElseThrow(CommentNotFoundException::new);
        return commentsMapper.toCommentsDtoResponse(comment);
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME_FOR_COMMENTS, key = "T(String).format('%s-%d-%d-%s-%s-%s', " +
                                                           "#searchElement, #page, #pageSize, " +
                                                           "#searchableFields, #sortField, #sortOrder)")
    public List<CommentsDtoResponse> fullTextSearchByTextAndUsernameField(
            String searchElement, int page, int pageSize, String searchableFields, String sortField, SortOrder sortOrder) {
        List<Comment> comments = commentsLuceneRepository
                .fullTextSearch(searchElement, page, pageSize, List.of(searchableFields), sortField, sortOrder);
        return commentsMapper.toCommentsDtoResponseList(comments);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CACHE_NAME_FOR_COMMENTS, key = "#result.id")
    public CommentsDtoResponse create(UUID newsUuid, CommentDtoRequest commentDtoRequest) {
        Comment comment = commentsMapper.toComment(commentDtoRequest);
        comment.setTime(Instant.now());
        String userName = getUserNameFromContext();
        comment.setUsername(userName);
        News news = newsRepository.findById(newsUuid).orElseThrow(NewsNotFoundException::new);
        comment.setNews(news);
        commentsRepository.save(comment);
        log.info("Comment created successfully at time: {}", comment.getTime());
        return commentsMapper.toCommentsDtoResponse(comment);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CACHE_NAME_FOR_COMMENTS, key = KAY_FOR_CACHE_COMMENTS)
    public CommentsDtoResponse update(UUID uuid, CommentDtoRequestUpdate commentDtoRequestUpdate) {
        Comment comment = commentsRepository.findById(uuid)
                .orElseThrow(() -> {
                    log.info(COMMENT_NOT_FOUND + uuid);
                    return new CommentNotFoundException();
                });

        Optional.ofNullable(commentDtoRequestUpdate.getText()).ifPresent(comment::setText);

        if (!getUserNameFromContext().equals(comment.getUsername())) {
            throw new UserNameNotFoundException();
        }

        Comment updatedComment = commentsRepository.save(comment);
        log.info("Comment updated successfully at time: {}", updatedComment.getTime());
        return commentsMapper.toCommentsDtoResponse(updatedComment);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME_FOR_COMMENTS, key = KAY_FOR_CACHE_COMMENTS)
    public void delete(UUID uuid) {
        int commentDeletedCount = commentsRepository.deleteIfExists(uuid, getUserNameFromContext());
        if (commentDeletedCount == 0) {
            log.info(COMMENT_NOT_FOUND + uuid);
            throw new CommentNotFoundException();
        }
        log.info("Comment deleted successfully with id : {}", uuid);
    }
}
