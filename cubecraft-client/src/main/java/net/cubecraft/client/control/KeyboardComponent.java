package net.cubecraft.client.control;

import ink.flybird.quantum3d.device.KeyboardButton;

public class KeyboardComponent implements ControlComponent{
    private final KeyboardButton component;

    public KeyboardComponent(KeyboardButton component) {
        this.component = component;
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
