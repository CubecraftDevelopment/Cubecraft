package io.flybird.cubecraft.auth;

public class Session {
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
