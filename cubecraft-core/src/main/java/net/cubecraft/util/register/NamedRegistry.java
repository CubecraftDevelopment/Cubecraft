package net.cubecraft.util.register;

import me.gb2022.commons.Initializable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

public final class NamedRegistry<I> {
    private final Collection<Consumer<Registered<I>>> handlers = new ArrayList<>();
    private final List<I> directContainer = new ArrayList<>(256);
    private final List<Registered<I>> list = new ArrayList<>(256);
    private final Map<String, Registered<I>> link = new HashMap<>();
    private int current = 0;

    public Registered<I> register(String name, I object) {
        Registered<I> rec = new Registered<>(object, name, this.current);

        this.directContainer.add(this.current, object);
        this.list.add(this.current, rec);
        this.link.put(name, rec);

        if (object instanceof Initializable i) {
            i.init();
        }

        this.current++;

        for (Consumer<Registered<I>> handler : this.handlers) {
            handler.accept(rec);
        }

        return rec;
    }

    public Registered<I> register(I object) {
        return register(((Named) object).getName(), object);
    }

    public I object(int id) {
        return this.directContainer.get(id);
    }

    public I object(String name) {
        return this.registered(name).get();
    }

    public String name(int id) {
        return this.registered(id).getName();
    }

    public int id(String name) {
        return this.registered(name).getId();
    }

    @SuppressWarnings("unchecked")// I know this is safe
    public Registered<I> registered(String name) {
        return Optional.ofNullable(this.link.get(name)).orElse((Registered<I>) Registered.DUMMY);
    }

    public Registered<I> registered(int id) {
        return this.list.get(id);
    }

    @SuppressWarnings("unchecked")// I know this is safe
    public <V> V[] map(NamedRegistry<V> dest) {
        var list = new ArrayList<V>(this.list.size());

        for (var rec : this.link.values()) {
            list.set(rec.getId(), dest.object(rec.getName()));
        }

        return (V[]) list.toArray();
    }

    public Set<String> names() {
        return this.link.keySet();
    }

    public void withShadow(Consumer<Registered<I>> consumer) {
        for (var reg : entries()) {
            consumer.accept(reg);
        }
        this.handlers.add(consumer);
    }

    public Collection<Registered<I>> entries() {
        return this.list;
    }

    public void handle(Class<?> cl) {
        for (Field f : cl.getDeclaredFields()) {
            f.trySetAccessible();
            if (f.getAnnotation(Registry.class) == null && cl.getAnnotation(Registry.class) == null) {
                continue;
            }
            try {
                f.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
