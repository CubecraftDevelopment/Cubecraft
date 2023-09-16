package ink.flybird.cubecraft.server.server;

public enum ServerStatus {
    STARTUP("start_up", 1),
    NETWORK_STARTING("network_starting", 2),
    WORLD_LOADING("world_loading",3),
    STARTED("world_loading",4),
    RUNNING("running", 5),
    STOPPING("stopping", 6);

    final String status;
    final int id;

    ServerStatus(String status, int id) {
        this.status = status;
        this.id = id;
    }

    public static ServerStatus fromID(int status) {
        return switch (status) {
            case 1 -> STARTUP;
            case 2 -> NETWORK_STARTING;
            case 3 -> RUNNING;
            case 4 -> STOPPING;
            default -> throw new IllegalArgumentException("WTF is this?" + status);
        };
    }

    public String getStatus() {
        return status;
    }
}
