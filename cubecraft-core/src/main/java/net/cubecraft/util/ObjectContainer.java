package net.cubecraft.util;

public final class ObjectContainer<T> {
    private T obj;


    public ObjectContainer(T obj) {
        this.obj = obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public T getObj() {
        return obj;
    }
}
