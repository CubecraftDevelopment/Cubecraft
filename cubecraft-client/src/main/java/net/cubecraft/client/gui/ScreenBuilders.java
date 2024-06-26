package net.cubecraft.client.gui;

import net.cubecraft.client.gui.screen.ScreenBuilder;
import net.cubecraft.client.registry.ResourceRegistry;

public interface ScreenBuilders {
    ScreenBuilder TITLE_SCREEN = ScreenBuilder.xml(ResourceRegistry.TITLE_SCREEN);
}
