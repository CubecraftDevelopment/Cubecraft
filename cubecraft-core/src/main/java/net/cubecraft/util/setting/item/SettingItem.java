package net.cubecraft.util.setting.item;

public abstract class SettingItem<I> {
    private final String namespace;
    private final String path;

    public SettingItem(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getPath() {
        return this.path;
    }

    public abstract I getDefine();

    public abstract I getValue();

    public abstract void setValue(Object obj);


    public final String id() {
        return this.namespace + ":" + this.path;
    }

    public String serialize() {
        return "<not implemented>";
    }

    public boolean useCustomSerialization() {
        return false;
    }

    public void deserialize(String data) {
    }
}
