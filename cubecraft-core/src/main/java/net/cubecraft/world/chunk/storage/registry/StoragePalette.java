package net.cubecraft.world.chunk.storage.registry;

import me.gb2022.commons.nbt.NBTDataIO;
import net.cubecraft.util.register.NamedRegistry;

public abstract class StoragePalette<V> implements NBTDataIO {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 65536 / WIDTH / WIDTH;

    protected final NamedRegistry<V> registry;

    public StoragePalette(NamedRegistry<V> registry) {
        this.registry = registry;
    }

    public void transform(RegistryStorage<V> old, RegistryStorage<V> latest) {
    }

    public void register(RegistryStorage<V> storage) {
    }

    public abstract short local(int id);

    public abstract int global(short id);

    public short local(String id) {
        return local(this.registry.id(id));
    }

    public String globalId(short id) {
        return this.registry.name(global(id));
    }

    public abstract void gc();
}
