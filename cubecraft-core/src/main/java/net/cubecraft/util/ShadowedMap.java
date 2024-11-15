package net.cubecraft.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShadowedMap<K, V> implements Map<K, V> {
    private final Set<Map<K, V>> maps;

    @SafeVarargs
    public ShadowedMap(Map<K, V>... maps) {
        this.maps = new HashSet<>(List.of(maps));
    }

    public Set<Map<K, V>> listened() {
        return maps;
    }

    public void listen(Map<K, V> map) {
        maps.add(map);
    }

    public void unListen(Map<K, V> map) {
        maps.remove(map);
    }

    @Override
    public int size() {
        return this.maps.stream().mapToInt(Map::size).sum();
    }

    @Override
    public boolean isEmpty() {
        return this.maps.stream().anyMatch((m) -> !m.isEmpty());
    }

    @Override
    public boolean containsKey(Object key) {
        return this.maps.stream().anyMatch((m) -> m.containsKey(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return this.maps.stream().anyMatch((m) -> m.containsValue(value));
    }

    @Override
    public V get(Object key) {
        for (var map : this.maps) {
            if (map.containsKey(key)) {
                var v = map.get(key);

                if (v == null) {
                    continue;
                }

                return v;
            }
        }

        return null;
    }

    @Override
    public @Nullable V put(K key, V value) {
        throw new UnsupportedOperationException("put()");
    }

    @Override
    public V remove(Object key) {
        this.maps.forEach((m) -> m.remove(key));
        return null;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("put()");
    }

    @Override
    public void clear() {
        this.maps.forEach(Map::clear);
    }

    @Override
    public @NotNull Set<K> keySet() {
        var s = new HashSet<K>();
        this.maps.stream().map(Map::keySet).forEach(s::addAll);

        return s;
    }

    @Override
    public @NotNull Collection<V> values() {
        var s = new HashSet<V>();
        this.maps.stream().map(Map::values).forEach(s::addAll);

        return s;
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        var s = new HashSet<Entry<K, V>>();
        this.maps.stream().map(Map::entrySet).forEach(s::addAll);

        return s;
    }
}
