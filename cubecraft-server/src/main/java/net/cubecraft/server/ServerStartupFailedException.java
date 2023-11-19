package net.cubecraft.server;

public class ServerStartupFailedException extends RuntimeException {
    public ServerStartupFailedException(Exception e) {
        super(e);
    }
}
