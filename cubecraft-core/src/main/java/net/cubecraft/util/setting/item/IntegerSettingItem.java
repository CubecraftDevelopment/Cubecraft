package net.cubecraft.util.setting.item;

public final class IntegerSettingItem extends SettingItem<Integer> {
    private final Integer define;
    private Integer value;

    public IntegerSettingItem(String namespace, String path, Integer define) {
        super(namespace, path);
        this.define = define;
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
        this.value = ((Long) value).intValue();
    }
}
