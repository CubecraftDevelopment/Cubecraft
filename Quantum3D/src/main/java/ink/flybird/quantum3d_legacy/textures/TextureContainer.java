package ink.flybird.quantum3d_legacy.textures;

import ink.flybird.fcommon.registry.RegisterMap;

public class TextureContainer<T extends Texture> {
    final RegisterMap<T> mapping = new RegisterMap<>();

    public T get(String name) {
        return this.mapping.get(name);
    }

    public T set(String name, T t) {
        this.mapping.registerItem(name, t);
        return t;
    }

    public RegisterMap<T> getMapping() {
        return mapping;
    }

    public void bind(String name) {
        this.get(name).bind();
    }

    public void unbind(String name) {
        this.get(name).unbind();
    }
}
