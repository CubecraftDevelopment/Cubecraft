package net.cubecraft.util.setting.item;

public final class BoolSetting extends SettingItem<Boolean> {
    private final Boolean define;
    private Boolean value;

    public BoolSetting(String namespace, String path, boolean define) {
        super(namespace, path);
        this.define = define;
    }

    public BoolSetting(String path, boolean define) {
        this("*", path, define);
    }
    
    @Override
    public Boolean getDefine() {
        return this.define;
    }

    @Override
    public Boolean getValue() {
        return this.value == null ? this.define : this.value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (boolean) value;
    }
}
