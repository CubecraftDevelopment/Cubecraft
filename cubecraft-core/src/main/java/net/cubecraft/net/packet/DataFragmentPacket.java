package net.cubecraft.net.packet;

public interface DataFragmentPacket<T> extends Packet {
    void fetchData(T t);

    void injectData(T t);
}
