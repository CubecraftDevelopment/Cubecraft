package net.cubecraft.client.control;

import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.MouseButton;
import me.gb2022.quantum3d.device.event.AnyClickInputEvent;
import net.cubecraft.util.setting.item.SettingItem;

public final class InputSettingItem extends SettingItem<InputCommand[]> {
    private final InputCommand[] define;
    private InputCommand[] value;

    public InputSettingItem(String namespace, String path, InputCommand... define) {
        super(namespace, path);
        this.define = define;
    }

    @Override
    public InputCommand[] getDefine() {
        return this.define;
    }

    @Override
    public InputCommand[] getValue() {
        return this.value == null ? this.define : this.value;
    }

    @Override
    public void setValue(Object obj) {
        this.value = (InputCommand[]) obj;
    }

    public boolean isActive(Keyboard keyboard, Mouse mouse) {
        for (InputCommand v : this.getValue()) {
            if (!v.isActive(keyboard, mouse)) {
                return false;
            }
        }
        return true;
    }

    public boolean isTriggered(AnyClickInputEvent event) {
        return isTriggered(event.getKeyboard(), event.getMouse(), event.getKeyboardButton(), event.getMouseButton());
    }

    public boolean isTriggered(Keyboard keyboard, Mouse mouse, KeyboardButton kb, MouseButton mb) {
        for (InputCommand c : this.getValue()) {
            if (!c.isActive(keyboard, mouse) && !c.isTriggered(kb, mb)) {
                return false;
            }
        }
        return true;
    }
}
