package me.gb2022.quantum3d.lwjgl.deprecated.textures;

import me.gb2022.commons.registry.RegisterMap;

@SuppressWarnings("unchecked")
public class TextureContainer<T extends Texture> {
    final RegisterMap<T> mapping = (RegisterMap<T>) new RegisterMap<>(Texture.class);

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
