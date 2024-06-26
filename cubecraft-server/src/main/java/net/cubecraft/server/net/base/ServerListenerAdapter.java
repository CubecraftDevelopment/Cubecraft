package net.cubecraft.server.net.base;

public abstract class ServerListenerAdapter {
    protected final ServerListener listener;

    public ServerListenerAdapter(ServerListener listener) {
        this.listener = listener;
    }
}
