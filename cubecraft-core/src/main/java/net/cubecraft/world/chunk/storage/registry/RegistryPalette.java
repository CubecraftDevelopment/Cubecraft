package net.cubecraft.world.chunk.storage.registry;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import me.gb2022.commons.nbt.NBTTagCompound;
import net.cubecraft.util.register.NamedRegistry;

import java.util.HashSet;
import java.util.Set;

public final class RegistryPalette<V> extends StoragePalette<V> {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 65536 / WIDTH / WIDTH;

    private final Int2IntMap global2localMap = new Int2IntArrayMap();
    private final Int2IntMap local2GlobalMap = new Int2IntArrayMap();
    private final Set<RegistryStorage<V>> registered = new HashSet<>();

    private int allocation = Short.MIN_VALUE;

    public RegistryPalette(NamedRegistry<V> registry) {
        super(registry);
    }

    public void transform(RegistryStorage<V> old, RegistryStorage<V> latest) {
        this.registered.remove(old);
        this.registered.add(latest);
    }

    @Override
    public void register(RegistryStorage<V> storage) {
        this.registered.add(storage);
    }


    @Override
    public short local(int id) {
        if (this.global2localMap.containsKey(id)) {
            return ((short) this.global2localMap.get(id));
        }
        return allocate(id);
    }

    @Override
    public int global(short id) {
        return this.local2GlobalMap.get(id);
    }

    @Override
    public void gc() {
        if (true) {
            return;
        }

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

    private void updateBaseAllocationRef() {
        while (this.local2GlobalMap.containsKey(this.allocation)) {
            this.allocation++;
        }
    }

    public Int2IntMap getLocal2GlobalMap() {
        return local2GlobalMap;
    }

    public Int2IntMap getGlobal2localMap() {
        return global2localMap;
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

    public short getAllocation() {
        return (short) allocation;
    }

    public void setAllocation(int integer) {
        this.allocation = integer;
    }

    @Override
    public NBTTagCompound getData() {
        var data = new NBTTagCompound();

        this.gc();

        data.setShort("_allocation", this.getAllocation());

        var map = this.getLocal2GlobalMap();

        for (int l : map.keySet()) {
            var id = this.registry.name(map.get(l));
            data.setShort(id, (short) l);
        }

        data.setInteger("_palette_type", 1);

        return data;
    }

    @Override
    public void setData(NBTTagCompound p) {

        this.setAllocation(p.getShort("_allocation"));

        for (var s : p.getTagMap().keySet()) {
            if (s.startsWith("_")) {
                continue;
            }

            var id = this.registry.id(s);
            var v = p.getShort(s);

            this.getLocal2GlobalMap().put(v, id);
            this.getGlobal2localMap().put(id, v);
        }
    }


}
