package by.clevertec.unit.cache;

import by.clevertec.cache.policy.LFUCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class LFUCacheTest {

    @Mock
    private LFUCache lfuCache;

    @BeforeEach
    void setUp() {
        lfuCache = new LFUCache("testCache", 3);
    }

    @Test
    void testPutAndGet() {
        //given
        lfuCache.put("key1", "value1");
        lfuCache.put("key2", "value2");
        lfuCache.put("key3", "value3");

        //when then
        Assertions.assertEquals("value1", Objects.requireNonNull(lfuCache.get("key1")).get());
        Assertions.assertEquals("value2", Objects.requireNonNull(lfuCache.get("key2")).get());
        Assertions.assertEquals("value3", Objects.requireNonNull(lfuCache.get("key3")).get());
    }

    @Test
    void testLFUPolicyEviction() {
        //given
        lfuCache.put("key1", "value1");
        lfuCache.put("key2", "value2");
        lfuCache.put("key3", "value3");
        lfuCache.get("key1");
        lfuCache.put("key4", "value4");

        //when then
        assertNotNull(lfuCache.get("key1"));
        assertNull(lfuCache.get("key2"));
        assertNotNull(lfuCache.get("key3"));
        assertNotNull(lfuCache.get("key4"));
    }

    @Test
    void testClear() {
        //given
        lfuCache.put("key1", "value1");
        lfuCache.put("key2", "value2");
        lfuCache.clear();

        //when then
        assertNull(lfuCache.get("key1"));
        assertNull(lfuCache.get("key2"));
    }

    @Test
    void testEvictSpecificKey() {
        //given
        lfuCache.put("key1", "value1");
        lfuCache.put("key2", "value2");
        lfuCache.evict("key1");

        //when then
        assertNull(lfuCache.get("key1"));
        assertNotNull(lfuCache.get("key2"));
    }
}
