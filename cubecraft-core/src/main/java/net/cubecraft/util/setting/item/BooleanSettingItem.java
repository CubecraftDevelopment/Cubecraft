package net.cubecraft.util.setting.item;

public final class BooleanSettingItem extends SettingItem<Boolean> {
    private final Boolean define;
    private Boolean value;

    public BooleanSettingItem(String namespace, String path, Boolean define) {
        super(namespace, path);
        this.define = define;
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
