package ink.flybird.cubecraft.auth;

public final class Session {
    private String name;
    private final String type;

    public Session(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
}
