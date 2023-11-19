package net.cubecraft.auth;

import net.cubecraft.ContentRegistries;
import net.cubecraft.SharedContext;

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

    @Override
    public String toString() {
        return "%s(%s)".formatted(this.getName(), this.getService().getServiceName());
    }

    public SessionService getService(){
        return SharedContext.SESSION_SERVICE.get(this.getType());
    }
}
