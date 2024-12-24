package by.clevertec.repositories;

import by.clevertec.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, UUID> {
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :uuid")
    int deleteIfExists(@Param("uuid") UUID uuid);
}
