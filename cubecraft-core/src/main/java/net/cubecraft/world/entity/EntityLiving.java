package net.cubecraft.world.entity;

import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.math.MathHelper;
import ink.flybird.fcommon.math.hitting.HitBox;
import ink.flybird.fcommon.math.hitting.HitResult;
import ink.flybird.fcommon.math.hitting.RayTest;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import net.cubecraft.ContentRegistries;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.item.container.Container;
import net.cubecraft.world.item.container.Inventory;
import org.joml.Vector3d;

import java.util.Collection;
import java.util.List;

public abstract class EntityLiving extends Entity {
    private final Inventory inventory;
    public HitResult hitResult;

    public boolean sprinting;
    public boolean sneaking = false;
    protected boolean flying;

    private float health;

    public EntityLiving(IWorld world) {
        super(world);
        this.inventory = ContentRegistries.INVENTORY.create(this.getID());
    }

    public Vector3d getLookingAt() {
        Vector3d from = new Vector3d(x, y + 1.62, z);
        Vector3d to = MathHelper.getVectorForRotation(xRot, yRot - 180);
        return to.mul(getReachDistance()).add(from);
    }

    @Override
    public void tick() {
        this.hitResult = null;
        Vector3d from = getCameraPosition().add(x, y, z);
        this.hitResult = RayTest.trace(getWorld().getSelectionBox(this, from, getLookingAt()), from, getLookingAt());
        super.tick();
    }

    public Vector3d getCameraPosition() {
        return new Vector3d(0, 0, 0);
    }

    public double getReachDistance() {
        return 5;
    }

    @Override
    public Collection<HitBox> getHitBox() {
        return List.of(new HitBox("living_default", this.getCollisionBox()));
    }

    @Override
    public AABB getCollisionBoxSize() {
        return new AABB(-0.3, 0, -0.3, 0.3, 1.75, 0.3);
    }

    public abstract int getHealth();

    public abstract EntityItem[] getDrop();

    @Override
    public NBTTagCompound getData() {
        NBTTagCompound tag = super.getData();
        tag.setBoolean("flying", this.flying);
        tag.setCompoundTag("inventory", this.inventory.serialize());
        return tag;
    }

    @Override
    public void setData(NBTTagCompound tag) {
        this.flying = tag.getBoolean("flying");
        this.inventory.setData(tag.getCompoundTag("inventory"));
        super.setData(tag);
    }

    public void attack() {
        if (this.hitResult == null) {
            return;
        }
        if (this.hitResult.instanceOf(EntityLiving.class)) {
            Container.getItem(this.inventory.getActive()).onAttack(this.hitResult.getObject(EntityLiving.class));
        } else {
            Container.getItem(this.inventory.getActive()).onDig(this.hitResult.getObject(IBlockAccess.class));
        }
    }

    public void interact() {
        if (this.hitResult == null) {
            return;
        }
        if (this.hitResult.instanceOf(EntityLiving.class)) {
            Container.getItem(this.inventory.getActive()).onUse(this.hitResult.getObject(EntityLiving.class));
        } else {
            Container.getItem(this.inventory.getActive()).onUse(this.hitResult.getObject(IBlockAccess.class));
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isSprinting() {
        return this.sprinting;
    }

    public void setSprinting(boolean b) {
        this.sprinting = b;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public void setFlying(boolean b) {
        this.flying = b;
    }

    public boolean isSneaking() {
        return this.sneaking;
    }

    public void setSneaking(boolean b) {
        this.sneaking = b;
    }

    @Override
    public boolean shouldBeEffectedByGravity() {
        return !this.flying;
    }

    @Override
    public float getSpeed() {
        if (this.isFlying()) {
            return this.sprinting ? 0.55f : 0.2f;
        } else {
            if (this.inLiquid()) {
                return this.sprinting ? 0.05f : 0.045f;
            } else {
                if (this.isOnGround()) {
                    return this.sneaking ? 0.03f : this.sprinting ? 0.138f : 0.09f;
                } else {
                    return this.sprinting ? 0.0276f : 0.02f;
                }
            }
        }
    }
}
