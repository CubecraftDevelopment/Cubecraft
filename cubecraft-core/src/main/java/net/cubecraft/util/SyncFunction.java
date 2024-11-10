package net.cubecraft.util;

import java.util.function.Consumer;

//this is stupid, huh?
public abstract class SyncFunction {
    public SyncFunction() throws Throwable{
        throw new IllegalAccessException("no instance!");
    }

    synchronized public static <T> void accept(T object, Consumer<T> consumer) {
        consumer.accept(object);
    }
}
