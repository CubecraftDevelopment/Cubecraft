package net.cubecraft.util;

public interface NSStringDispatcher {
    static String getNameSpace(String all) {
        return all.split(":")[0];
    }

    static String getId(String all) {
        return all.split(":")[1];
    }

    static String combine(String namespace, String id) {
        return namespace + ":" + id;
    }
}
