package by.clevertec.integration;

import by.clevertec.cache.policy.LFUCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("testcontainers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:db/data.sql"})
@Transactional
class CacheIntegrationTest {

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testCustomCacheManagerWithLFUCache() {
        //given
        Cache cache = cacheManager.getCache("testCache");

        //when then
        assertNotNull(cache);
        assertTrue(cache instanceof LFUCache);

        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        assertEquals("value1", cache.get("key1").get());
    }
}
