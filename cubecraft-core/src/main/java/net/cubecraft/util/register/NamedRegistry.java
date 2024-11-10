package net.cubecraft.util.register;

import me.gb2022.commons.Initializable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public final class NamedRegistry<I> {
    private final Collection<Consumer<Registered<I>>> handlers = new ArrayList<>();
    private final List<I> directContainer = new ArrayList<>(256);
    private final List<Registered<I>> list = new ArrayList<>(256);
    private final Map<String, Registered<I>> link = new HashMap<>();
    private final Registered<I> fallback;
    private int current = 0;

    public NamedRegistry(Registered<I> dummy) {
        fallback = dummy;
    }


    public NamedRegistry() {
        this((Registered<I>) Registered.DUMMY);
    }

    public <T extends I> Registered<T> register(String name, T object) {
        var rec = ((Registered<T>) registered(name));

        if (rec == null || rec == this.fallback) {
            rec = new Registered<>(object, name, this.current);

            this.directContainer.add(this.current, object);
            this.list.add(this.current, (Registered<I>) rec);
            this.link.put(name, (Registered<I>) rec);

            this.current++;
        } else {
            this.directContainer.set(this.current - 1, object);
            rec._set(object);
        }

        if (object instanceof Initializable i) {
            i.init();
        }

        for (Consumer<Registered<I>> handler : this.handlers) {
            handler.accept((Registered<I>) rec);
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

    public Registered<I> registered(String name) {
        return Optional.ofNullable(this.link.get(name)).orElse(this.fallback);
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

    public int getCurrent() {
        return current;
    }

    public Registered<? extends I> deferred(String s) {
        var entry = new DeferredRegistered<I>();
        this.withShadow(r -> {
            if (!Objects.equals(r.getName(), s)) {
                return;
            }
            entry.inject(r);
        });
        return entry;
    }


    @SuppressWarnings("unchecked")
    public <T extends I> Registered<T> deferred(String id, Class<T> type) {
        return register(id, null);
    }

    public <T extends I> T object(String name, Class<T> type) {
        return type.cast(this.object(name));
    }

    public <T extends I> T object(int id, Class<T> type) {
        return type.cast(this.object(id));
    }

    public int[] ids() {
        var arr = new int[this.current];
        for (int i = 0; i < this.current; i++) {
            arr[i] = i;
        }
        return arr;
    }
}
