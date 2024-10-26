package net.cubecraft.world.storage;

import net.cubecraft.ContentRegistries;
import net.cubecraft.world.World;
import net.cubecraft.world.entity.Entity;

public interface PersistentEntityHolder {
    default Entity load(String uuid, World world, String type) {
        var e = ContentRegistries.ENTITY.create(type, world);
        load(e);
        return e;
    }

    boolean load(Entity e);

    void save(Entity entity);
}
