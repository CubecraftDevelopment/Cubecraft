package ink.flybird.cubecraft.client.event.gui.context;

import ink.flybird.cubecraft.client.event.gui.GUIEvent;
import ink.flybird.cubecraft.client.gui.GUIContext;

public abstract class GUIContextEvent extends GUIEvent {
    protected GUIContextEvent(GUIContext context) {
        super(context);
    }
}
