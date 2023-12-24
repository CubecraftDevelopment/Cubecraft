package net.cubecraft.client.control;

import me.gb2022.quantum3d.device.KeyboardButton;

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
