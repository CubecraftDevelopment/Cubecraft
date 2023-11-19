package net.cubecraft.util.setting.item;

public final class DoubleSettingItem extends SettingItem<Double> {
    private final Double define;
    private Double value;

    public DoubleSettingItem(String namespace, String path, Double define) {
        super(namespace, path);
        this.define = define;
    }

    @Override
    public Double getDefine() {
        return this.define;
    }

    @Override
    public Double getValue() {
        return this.value == null ? this.define : this.value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (Double) value;
    }
}
