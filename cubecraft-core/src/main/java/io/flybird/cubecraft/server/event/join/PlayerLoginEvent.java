package io.flybird.cubecraft.server.event.join;

import io.flybird.cubecraft.auth.Session;

public class PlayerLoginEvent{
    private final Session session;
    private boolean allow;

    public PlayerLoginEvent(Session session) {
        this.session = session;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    public boolean isAllow() {
        return allow;
    }

    public Session getSession() {
        return session;
    }
}