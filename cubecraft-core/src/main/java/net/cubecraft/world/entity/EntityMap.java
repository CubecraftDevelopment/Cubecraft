package net.cubecraft.world.entity;

import net.cubecraft.world.IWorld;
import ink.flybird.fcommon.math.AABB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class EntityMap {
    private final HashMap<String, List<String>> uuids = new HashMap<>();
    private final HashMap<String, List<String>> locations = new HashMap<>();


    private final IWorld world;

    public EntityMap(IWorld world) {
        this.world = world;
    }

    public List<String> getUUIDListAt(long x, long y, long z) {
        String loc = this.encodePosition(x, y, z);
        if (!this.uuids.containsKey(loc)) {
            this.uuids.put(loc, new ArrayList<>());
        }
        return this.uuids.get(loc);
    }

    private String encodePosition(long x, long y, long z) {
        return x + "/" + y + "/" + z;
    }

    public void removeEntityAt(long x, long y, long z) {
        for (Entity e : getEntitiesByBlock(x, y, z)) {
            this.removeEntity(e);
            this.world.removeEntity(e);
        }
    }

    public List<Entity> getEntitiesByBlock(long x, long y, long z) {
        List<Entity> result = new ArrayList<>();
        List<String> lst=getUUIDListAtWithNoAbsentCheck(x, y, z);
        if(lst==null){
            return result;
        }
        String[] uids = lst.toArray(new String[0]);

        for (String s : uids) {
            Entity e = this.world.getEntity(s);
            if (result.contains(e)) {
                continue;
            }
            result.add(this.world.getEntity(s));
        }
        return result;
    }

    private List<String> getUUIDListAtWithNoAbsentCheck(long x, long y, long z) {
        return this.uuids.get(encodePosition(x, y, z));
    }


    public void addEntityByUID(String uuid) {
        this.addEntity(this.world.getEntity(uuid));
    }

    public void removeEntityByUID(String uuid) {
        this.removeEntity(this.world.getEntity(uuid));
    }

    public void addEntity(Entity entity) {
        this.add(entity.getCollisionBox(), entity.getUuid());
    }

    public void removeEntity(Entity entity) {
        this.remove(entity.getCollisionBox(), entity.getUuid());
    }

    private void remove(AABB box, String uid) {
        long xx0 = (long) Math.floor(box.x0);
        long yy0 = (long) Math.floor(box.y0);
        long zz0 = (long) Math.floor(box.z0);
        long xx1 = (long) Math.ceil(box.x1);
        long yy1 = (long) Math.ceil(box.y1);
        long zz1 = (long) Math.ceil(box.z1);
        for (long i = xx0; i <= xx1; i++) {
            for (long j = yy0; j <= yy1; j++) {
                for (long k = zz0; k <= zz1; k++) {
                    List<String> uuids = getUUIDListAtWithNoAbsentCheck(i, j, k);
                    if(uuids==null){
                        continue;
                    }
                    uuids.remove(uid);
                    if (uuids.isEmpty()) {
                        this.uuids.remove(encodePosition(i, j, k));
                    }
                }
            }
        }
    }

    private void add(AABB box, String uid) {
        long xx0 = (long) Math.floor(box.x0);
        long yy0 = (long) Math.floor(box.y0);
        long zz0 = (long) Math.floor(box.z0);
        long xx1 = (long) Math.ceil(box.x1);
        long yy1 = (long) Math.ceil(box.y1);
        long zz1 = (long) Math.ceil(box.z1);

        for (long i = xx0; i <= xx1; i++) {
            for (long j = yy0; j <= yy1; j++) {
                for (long k = zz0; k <= zz1; k++) {
                    List<String> uuids = getUUIDListAt(i, j, k);
                    uuids.add(uid);
                }
            }
        }
    }

    public void update(Entity e) {
        this.remove(e.getLastCollisionBox(), e.getUuid());
        this.add(e.getCollisionBox(), e.getUuid());
    }

    public IWorld getWorld() {
        return this.world;
    }
}
