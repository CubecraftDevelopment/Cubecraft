package net.cubecraft.util.register;

import java.util.*;

public final class ShadowedRegistry<I, S> {
    private final NamedRegistry<S> shadowed;
    private final List<I> list = new ArrayList<>(256);
    private final Map<String, I> map = new HashMap<>(256);

    public ShadowedRegistry(NamedRegistry<S> shadowed) {
        this.shadowed = shadowed;
    }

    public int register(String id, I obj) {
        var index = this.shadowed.id(id);

        if (index == -1) {
            throw new IllegalArgumentException("item with id " + id + " not found in shadowed registry");
        }

        for (var i = this.list.size() - 1; i < index + 2; i++) {
            this.list.add(null);//expand
        }

        this.list.set(index, obj);
        this.map.put(id, obj);

        return index;
    }

    public I get(String id) {
        return this.map.get(id);
    }

    public I get(int index) {
        return this.list.get(index);
    }

    public boolean isPresent(String id) {
        return this.map.containsKey(id);
    }

    public Collection<I> values() {
        return this.map.values();
    }
}
