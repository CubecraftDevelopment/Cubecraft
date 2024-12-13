package net.cubecraft.util.setting.item;

public final class FloatSetting extends SettingItem<Double> {
    private final double define;
    private double value = Double.NaN;

    public FloatSetting(String namespace, String path, double define) {
        super(namespace, path);
        this.define = define;
    }

    public FloatSetting(String path, double define) {
        this("*", path, define);
    }

    @Override
    public Double getDefine() {
        return this.define;
    }

    @Override
    public Double getValue() {
        return Double.isNaN(this.value) ? this.define : this.value;
    }

    @Override
    public void setValue(Object value) {
        this.value = ((Number) value).doubleValue();
    }
}
