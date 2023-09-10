package ink.flybird.cubecraft.client.event;

public class ScopedKeypressEvent {
    private final String action;
    private final String scope;

    public ScopedKeypressEvent(String action, String scope) {
        this.action = action;
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    public String getAction() {
        return action;
    }
}
