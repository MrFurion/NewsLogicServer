package by.clevertec.integration.repositories;


import by.clevertec.repositories.NewsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:db/data.sql"})
class NewsRepositoryIntegrationTest {

    @Autowired
    private NewsRepository newsRepository;


    @Test
    void deleteIfExists() {

        //given
        UUID id = UUID.fromString("1e1a3208-dfc5-4eb7-8d8e-1f50f31dc70a");
        int expected = 1;

        //when
        Assertions.assertTrue(newsRepository.findById(id).isPresent());
        int actual = newsRepository.deleteIfExists(id);

        //then
        Assertions.assertEquals(expected, actual);
    }
}