package net.cubecraft.util.setting.item;

public final class IntSetting extends SettingItem<Integer> {
    private final Integer define;
    private Integer value;

    public IntSetting(String namespace, String path, Integer define) {
        super(namespace, path);
        this.define = define;
    }

    public IntSetting(String path, Integer define) {
        this("*", path, define);
    }

    @Override
    public Integer getDefine() {
        return this.define;
    }

    @Override
    public Integer getValue() {
        return this.value == null ? this.define : this.value;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Integer val) {
            this.value = val;
            return;
        }
        this.value = ((Number) value).intValue();
    }
}
