package by.clevertec.lucene.repository;

import by.clevertec.models.Comment;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentsLuceneRepository extends BaseRepository<Comment, UUID> {
}
