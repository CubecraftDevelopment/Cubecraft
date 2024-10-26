package net.cubecraft.world.block;

import me.gb2022.commons.Initializable;
import me.gb2022.commons.math.AABB;
import me.gb2022.commons.math.hitting.HitBox;
import net.cubecraft.ContentRegistries;
import net.cubecraft.event.register.BlockRegisterEvent;
import net.cubecraft.util.register.Named;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.behavior.BlockBehavior;
import net.cubecraft.world.block.property.BlockProperty;
import net.cubecraft.world.block.property.BooleanProperty;

import java.util.*;

public abstract class Block implements Initializable, Named {
    private final HashMap<String, BlockProperty<?>> properties = new HashMap<>(64);
    private final HashMap<String, BlockBehavior> behaviors = new HashMap<>(64);
    private final String id;
    boolean cachedSolidValue = true;

    public Block(String id) {
        this.id = id;
    }

    @Override
    public void init() {
        this.applyData(this.getBehaviorList());
    }

    private void applyData(String[] behaviorList) {
        this.initPropertyMap(this.properties);
        for (String id : behaviorList) {
            BlockBehavior behavior = ContentRegistries.BLOCK_BEHAVIOR.get(id);
            if (behavior == null) {
                continue;
            }
            this.behaviors.put(id, behavior);
        }
        BooleanProperty prop = getBlockProperty("cubecraft:solid", BooleanProperty.class);
        if (prop != null) {
            this.cachedSolidValue = prop.get(null);
        }
        ContentRegistries.EVENT_BUS.callEvent(new BlockRegisterEvent(this.id, this));
    }


    public abstract void initPropertyMap(Map<String, BlockProperty<?>> map);

    public abstract String[] getBehaviorList();


    public <T> T getBlockProperty(String id, Class<T> type) {
        return type.cast(this.properties.get(id));
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


    public int opacity() {
        return 0;
    }

    public int getLight() {
        return 0;
    }


    public void onBlockUpdate(IBlockAccess block) {
        for (BlockBehavior behavior : this.getBehaviors()) {
            behavior.onBlockTick(block);
        }
    }

    public String getID() {
        return this.id;
    }

    public String[] getTags() {
        return new String[0];
    }

    public Collection<HitBox> getSelectionBox(long x, long y, long z) {
        Collection<HitBox> result = new ArrayList<>();
        for (int i = 0; i < getSelectionBoxSizes().length; i++) {
            AABB aabb = getCollisionBoxSizes()[i];
            AABB copied = new AABB(x + aabb.x0, y + aabb.y0, z + aabb.z0, x + aabb.x1, y + aabb.y1, z + aabb.z1);
            result.add(new HitBox("default", copied));
        }
        return result;
    }

    public BlockState defaultState(long x, long y, long z) {
        return new BlockState(this.getID(), (byte) 0, (byte) 0).setX(x).setY(y).setZ(z);
    }

    public int light() {
        return 0;
    }

    public boolean queryBoolean(String s, IBlockAccess blockAccess) {
        return switch (s) {
            case "face_top" -> blockAccess.getBlockFacing() == EnumFacing.Up;
            case "face_bottom" -> blockAccess.getBlockFacing() == EnumFacing.Down;
            case "face_left" -> blockAccess.getBlockFacing() == EnumFacing.West;
            case "face_right" -> blockAccess.getBlockFacing() == EnumFacing.East;
            case "face_front" -> blockAccess.getBlockFacing() == EnumFacing.South;
            case "face_back" -> blockAccess.getBlockFacing() == EnumFacing.North;
            case "default" -> true;
            default -> false;
        };
    }

    public boolean isLiquid() {
        return Arrays.asList(this.getTags()).contains("cubecraft:liquid");
    }

    public Map<String, BlockProperty<?>> getProperties() {
        return this.properties;
    }

    public boolean isSolid() {
        return this.cachedSolidValue;
    }


    @Override
    public String getName() {
        return this.id;
    }
}
