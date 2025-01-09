package by.clevertec.unit.cache;

import by.clevertec.cache.policy.LRUCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class LRUCacheTest {

    @Mock
    private LRUCache lruCache;

    @BeforeEach
    void setUp() {
        lruCache = new LRUCache("testCache", 3);
    }

    @Test
    void testPutAndGet() {
        //given
        lruCache.put("key1", "value1");
        lruCache.put("key2", "value2");
        lruCache.put("key3", "value3");

        //when then
        assertEquals("value1", Objects.requireNonNull(lruCache.get("key1")).get());
        assertEquals("value2", Objects.requireNonNull(lruCache.get("key2")).get());
        assertEquals("value3", Objects.requireNonNull(lruCache.get("key3")).get());
    }

    @Test
    void testLRUPolicyEviction() {
        //given
        lruCache.put("key1", "value1");
        lruCache.put("key2", "value2");
        lruCache.put("key3", "value3");
        lruCache.get("key1");
        lruCache.put("key4", "value4");

        //when then
        assertNotNull(lruCache.get("key1"));
        assertNull(lruCache.get("key2"));
        assertNotNull(lruCache.get("key3"));
        assertNotNull(lruCache.get("key4"));
    }

    @Test
    void testClear() {
        //given
        lruCache.put("key1", "value1");
        lruCache.put("key2", "value2");
        lruCache.clear();

        //when then
        assertNull(lruCache.get("key1"));
        assertNull(lruCache.get("key2"));
    }
}
