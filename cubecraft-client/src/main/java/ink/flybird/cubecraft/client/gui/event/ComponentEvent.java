package ink.flybird.cubecraft.client.gui.event;

import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.gui.GUIManager;


import java.util.Objects;

public abstract class ComponentEvent {
    protected final Node component;
    protected final Screen screen;
    protected final GUIManager context;

    protected ComponentEvent(Node component, Screen screen, GUIManager context) {
        this.component = component;
        this.screen = screen;
        this.context = context;
    }

    public Screen getScreen() {
        return screen;
    }

    public String getComponentID() {
        return this.component.getId();
    }

    public String getScreenID() {
        return this.screen.getID();
    }

    public boolean isTargetComponent(String componentID, String screenID) {
        return Objects.equals(componentID, this.getComponentID()) && Objects.equals(screenID, this.screen.getID());
    }

    public GUIManager getContext() {
        return context;
    }
}
