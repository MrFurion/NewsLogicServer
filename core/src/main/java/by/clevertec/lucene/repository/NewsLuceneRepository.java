package by.clevertec.lucene.repository;

import by.clevertec.models.News;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NewsLuceneRepository extends BaseRepository<News, UUID> {
}
