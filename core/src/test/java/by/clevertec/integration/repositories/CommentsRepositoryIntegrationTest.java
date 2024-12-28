package by.clevertec.integration.repositories;

import by.clevertec.repositories.CommentsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

@DataJpaTest
@ActiveProfiles("testcontainers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:db/data.sql"})
class CommentsRepositoryIntegrationTest {

    @Autowired
    private CommentsRepository commentsRepository;

    @Test
    void deleteIfExists() {

        //given
        UUID id = UUID.fromString("28ab736b-3f20-4cd1-bb87-2c05e06ea4ab");
        int expected = 1;

        //when
        Assertions.assertTrue(commentsRepository.findById(id).isPresent());
        int actual = commentsRepository.deleteIfExists(id);

        //then
        Assertions.assertEquals(expected, actual);
    }
}
