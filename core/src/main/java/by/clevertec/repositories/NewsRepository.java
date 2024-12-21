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
    @Query("DELETE FROM News n WHERE n.id = :uuid")
    int deleteIfExists(@Param("uuid") UUID uuid);
}
