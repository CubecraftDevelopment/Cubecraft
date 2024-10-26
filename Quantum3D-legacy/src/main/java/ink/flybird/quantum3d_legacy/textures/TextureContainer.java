package ink.flybird.quantum3d_legacy.textures;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class TextureContainer<T extends Texture> {
    final Map<String,T> mapping = new HashMap<>();

    public T get(String name) {
        return this.mapping.get(name);
    }

    public T set(String name, T t) {
        this.mapping.put(name, t);
        return t;
    }

    public Map<String,T> getMapping() {
        return mapping;
    }

    public void bind(String name) {
        this.get(name).bind();
    }

    public void unbind(String name) {
        this.get(name).unbind();
    }
}
