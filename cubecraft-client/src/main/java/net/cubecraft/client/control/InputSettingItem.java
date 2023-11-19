package net.cubecraft.client.control;

import net.cubecraft.util.setting.item.SettingItem;

public final class InputSettingItem extends SettingItem<ControlComponent[]> {
    private final ControlComponent[] define;
    private ControlComponent[] value;

    public InputSettingItem(String namespace, String path, ControlComponent... define) {
        super(namespace, path);
        this.define = define;
    }

    @Override
    public ControlComponent[] getDefine() {
        return this.define;
    }

    @Override
    public ControlComponent[] getValue() {
        return this.value==null?this.define:this.value;
    }

    @Override
    public void setValue(Object obj) {
        this.value= (ControlComponent[]) obj;
    }

    public void isActive(){

    }
}
