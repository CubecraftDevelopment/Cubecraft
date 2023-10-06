package ink.flybird.cubecraft.client.event.gui.component;

import ink.flybird.cubecraft.client.event.gui.GUIEvent;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.gui.GUIContext;


import java.util.Objects;

public abstract class ComponentEvent extends GUIEvent {
    protected final Node component;
    protected final Screen screen;


    protected ComponentEvent(Node component, Screen screen, GUIContext context) {
        super(context);
        this.component = component;
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }

    public String getComponentID() {
        return this.component.getId();
    }

    public String getScreenID() {
        return this.screen.getId();
    }

    public boolean isTargetComponent(String componentID, String screenID) {
        return Objects.equals(componentID, this.getComponentID()) && Objects.equals(screenID, this.screen.getId());
    }
}
