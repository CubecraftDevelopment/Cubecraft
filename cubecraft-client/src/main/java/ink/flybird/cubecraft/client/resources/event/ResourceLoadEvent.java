package ink.flybird.cubecraft.client.resources.event;

public abstract class ResourceLoadEvent {
    private final String stage;

    protected ResourceLoadEvent(String stage) {
        this.stage = stage;
    }

    public String getStage() {
        return stage;
    }
}
