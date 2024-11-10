package net.cubecraft.world.block;

import me.gb2022.commons.Initializable;
import me.gb2022.commons.math.AABB;
import net.cubecraft.ContentRegistries;
import net.cubecraft.util.register.Named;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.behavior.BlockBehavior;
import net.cubecraft.world.block.property.BlockProperty;
import net.cubecraft.world.block.property.BooleanProperty;
import net.cubecraft.world.block.property.IntProperty;
import net.cubecraft.world.block.property.collision.BlockCollisionProperty;
import net.cubecraft.world.block.property.collision.CollisionProperty;
import net.cubecraft.world.block.property.hitbox.BlockHitBoxProperty;
import net.cubecraft.world.block.property.hitbox.HitBoxProperty;

import java.util.*;

public abstract class Block implements Initializable, Named {
    private final HashMap<String, BlockProperty<?>> properties = new HashMap<>(64);
    private final HashMap<String, BlockBehavior> behaviors = new HashMap<>(64);
    private final String id;
    boolean cachedSolidValue = true;

    private CollisionProperty collision = new BlockCollisionProperty();
    private HitBoxProperty selection = new BlockHitBoxProperty();
    private boolean solid = true;
    private int light = 0;
    private int opacity = 0;

    public Block(String id) {
        this.id = id;
    }

    @Override
    public void init() {
        this.initPropertyMap(this.properties);
        for (var id : this.getBehaviorList()) {
            BlockBehavior behavior = ContentRegistries.BLOCK_BEHAVIOR.get(id);
            if (behavior == null) {
                continue;
            }
            this.behaviors.put(id, behavior);
        }

        getBlockProperty("cubecraft:solid", BooleanProperty.class).ifPresent((p) -> this.solid = p.get(null));
        getBlockProperty("cubecraft:light", IntProperty.class).ifPresent((p) -> this.light = p.get(null));
        getBlockProperty("cubecraft:collision", CollisionProperty.class).ifPresent((p) -> this.collision = p);
        getBlockProperty("cubecraft:selection", HitBoxProperty.class).ifPresent((p) -> this.selection = p);
    }


    public abstract void initPropertyMap(Map<String, BlockProperty<?>> map);

    public abstract String[] getBehaviorList();

    public <T> Optional<T> getBlockProperty(String id, Class<T> type) {
        return Optional.ofNullable(type.cast(this.properties.get(id)));
    }

    public Collection<BlockBehavior> getBehaviors() {
        return this.behaviors.values();
    }

    public BlockBehavior getBehavior(String id) {
        return this.behaviors.get(id);
    }


    public EnumFacing[] getEnabledFacings() {
        return new EnumFacing[]{EnumFacing.Up};
    }

    public AABB[] getCollisionBoxSizes() {
        return new AABB[0];
    }

    public AABB[] getSelectionBoxSizes() {
        return new AABB[0];
    }

    public float getResistance() {
        return 0;
    }

    public float getDensity() {
        return 0;
    }

    public float getHardNess() {
        return 0;
    }


    public CollisionProperty getCollision() {
        return collision;
    }

    public HitBoxProperty getSelection() {
        return selection;
    }

    public int getLight() {
        return this.light;
    }

    public boolean isSolid() {
        return this.solid;
    }

    public int opacity() {
        return this.opacity;
    }




    public void onBlockUpdate(BlockAccess block) {
        for (BlockBehavior behavior : this.getBehaviors()) {
            behavior.onBlockTick(block);
        }
    }

    public String getID() {
        return this.id;
    }

    public String[] getTags() {
        return getBehaviorList();
    }

    public BlockState defaultState(long x, long y, long z) {
        return new BlockState(this.getID(), (byte) 0, (byte) 0).setX(x).setY(y).setZ(z);
    }

    public int light() {
        return 0;
    }

    public boolean queryBoolean(String s, BlockAccess blockAccess) {
        return switch (s) {
            default -> false;
        };
    }

    public boolean isLiquid() {
        return Arrays.asList(this.getTags()).contains("cubecraft:liquid");
    }

    public Map<String, BlockProperty<?>> getProperties() {
        return this.properties;
    }


    @Override
    public String getName() {
        return this.id;
    }
}
