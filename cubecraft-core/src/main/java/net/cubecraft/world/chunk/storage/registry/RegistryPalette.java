package net.cubecraft.world.chunk.storage.registry;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.cubecraft.util.register.NamedRegistry;

import java.util.HashSet;
import java.util.Set;

public final class RegistryPalette<V> {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 65536 / WIDTH / WIDTH;

    private final NamedRegistry<V> registry;
    private final Int2IntMap global2localMap = new Int2IntArrayMap();
    private final Int2IntMap local2GlobalMap = new Int2IntArrayMap();
    private final Set<RegistryStorage<V>> registered = new HashSet<>();

    private int allocation = Short.MIN_VALUE;

    public RegistryPalette(NamedRegistry<V> registry) {
        this.registry = registry;
    }

    public void transform(RegistryStorage<V> old, RegistryStorage<V> latest) {
        this.registered.remove(old);
        this.registered.add(latest);
    }

    public void register(RegistryStorage<V> storage) {
        this.registered.add(storage);
    }

    public short allocate(int id) {
        if (this.allocation > 32760) {
            this.gc();
        }

        this.updateBaseAllocationRef();

        var allocated = this.allocation;
        this.allocation++;

        this.global2localMap.put(id, allocated);
        this.local2GlobalMap.put(allocated, id);

        return (short) allocated;
    }

    public short local(int id) {
        if (this.global2localMap.containsKey(id)) {
            return ((short) this.global2localMap.get(id));
        }

        return allocate(id);
    }

    public short local(String id) {
        return local(this.registry.id(id));
    }

    public int global(short id) {
        return this.local2GlobalMap.get(id);
    }

    public String globalId(short id) {
        return this.registry.name(global(id));
    }

    private void updateBaseAllocationRef() {
        while (this.local2GlobalMap.containsKey(this.allocation)) {
            this.allocation++;
        }
    }

    public void gc() {
        var map = new Int2IntOpenHashMap(8192);//local - global

        for (RegistryStorage<V> storage : this.registered) {
            for (var id : storage.getData()) {
                if (map.containsKey(id)) {
                    continue;
                }

                map.put(id, global(id));
            }
        }

        this.local2GlobalMap.clear();
        this.local2GlobalMap.putAll(map);

        this.global2localMap.clear();
        for (var node : map.int2IntEntrySet()) {
            this.global2localMap.put(node.getIntValue(), node.getIntKey());
        }

        this.allocation = 0;
        this.updateBaseAllocationRef();
    }

    public Int2IntMap getLocal2GlobalMap() {
        return local2GlobalMap;
    }

    public Int2IntMap getGlobal2localMap() {
        return global2localMap;
    }

    public short getAllocation() {
        return (short) allocation;
    }

    public void setAllocation(int integer) {
        this.allocation = integer;
    }
}
