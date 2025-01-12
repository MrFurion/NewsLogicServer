package by.clevertec.repositories;

import by.clevertec.models.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NewsRepository extends JpaRepository<News, UUID> {
    @Modifying
    @Query(value = "DELETE FROM news WHERE id = :uuid AND EXISTS (" +
                   "SELECT 1 FROM client_name WHERE client_id = :uuid AND username = :username)",
            nativeQuery = true)
    int deleteIfExists(@Param("uuid") UUID uuid, @Param("username") String username);
}
