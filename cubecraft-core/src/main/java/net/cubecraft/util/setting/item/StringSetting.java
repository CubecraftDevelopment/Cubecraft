package net.cubecraft.util.setting.item;

public final class StringSetting extends SettingItem<String> {
    private final String define;
    private String value;

    public StringSetting(String namespace, String path, String define) {
        super(namespace, path);
        this.define = define;
    }

    public StringSetting(String path, String define) {
        super("*", path);
        this.define = define;
    }
    
    @Override
    public String getDefine() {
        return this.define;
    }

    @Override
    public String getValue() {
        return this.value == null ? this.define : this.value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value.toString();
    }
}
