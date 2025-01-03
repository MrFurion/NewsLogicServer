package by.clevertec.cache.policy;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
public class LRUCache implements Cache {

    private final String name;
    private final int capacity;
    private final Map<Object, Node> cache;
    private final LinkedList<Node> accessOrder;

    public LRUCache(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);
        this.accessOrder = new LinkedList<>();
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public Object getNativeCache() {
        return cache;
    }

    @Override
    public ValueWrapper get(@NotNull Object key) {
        Node node = cache.get(key);
        if (node == null) {
            return null;
        }

        accessOrder.remove(node);
        accessOrder.addFirst(node);
        return new SimpleValueWrapper(node.value);
    }

    @Override
    public <T> T get(@NotNull Object key, Class<T> type) {
        Node node = cache.get(key);
        if (node == null) {
            return null;
        }

        Object value = node.value;
        if (type.isInstance(value)) {
            accessOrder.remove(node);
            accessOrder.addFirst(node);
            return type.cast(value);
        }
        return null;
    }

    @Override
    public <T> T get(@NotNull Object key, @NotNull Callable<T> valueLoader) {
        Node node = cache.get(key);
        if (node != null) {
            accessOrder.remove(node);
            accessOrder.addFirst(node);
            return (T) node.value;
        }

        try {
            T value = valueLoader.call();
            put(key, value);
            return value;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new RuntimeException("Error loading value for key: " + key, e);
        }
    }

    @Override
    public void put(@NotNull Object key, Object value) {
        Node newNode = new Node(key, value);

        if (cache.size() >= capacity) {
            Node leastUsed = accessOrder.removeLast();
            cache.remove(leastUsed.key);
        }

        cache.put(key, newNode);
        accessOrder.addFirst(newNode);
    }

    @Override
    public void evict(@NotNull Object key) {
        Node node = cache.remove(key);
        if (node != null) {
            accessOrder.remove(node);
        }
    }

    @Override
    public void clear() {
        cache.clear();
        accessOrder.clear();
    }

    private static class Node {
        private final Object key;
        private final Object value;

        public Node(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}
