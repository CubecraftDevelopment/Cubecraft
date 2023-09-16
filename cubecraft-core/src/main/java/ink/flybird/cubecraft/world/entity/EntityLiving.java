package ink.flybird.cubecraft.world.entity;

import ink.flybird.cubecraft.register.ContentRegistries;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.entity.item.Item;
import ink.flybird.cubecraft.world.item.Inventory;
import ink.flybird.fcommon.math.*;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import org.joml.Vector3d;

public abstract class EntityLiving extends Entity {
    public HitResult<Entity, IWorld> hitResult;
    public boolean runningMode;
    public boolean flying = false;
    public boolean sneak = false;
    private Inventory inventory;
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
        this.hitResult = RayTest.rayTrace(world.getSelectionBox(this, from, getLookingAt()), from, getLookingAt());
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.inLiquid()) {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.8f;
            this.yd *= 0.8f;
            this.zd *= 0.8f;
            if (!flying) {
                this.yd -= 0.08;
            }
            if (this.horizontalCollision && this.isFree(this.xd, this.yd + 0.6f - this.y + yo, this.zd)) {
                this.yd = 0.3f;
            }
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.91f;
            this.yd *= 0.98f;
            this.zd *= 0.91f;
            if (!flying) {
                this.yd -= 0.08;
            } else {
                this.yd *= 0.5f;
            }
            if (this.onGround) {
                this.xd *= 0.6f;
                this.zd *= 0.6f;
            }
        }

    }

    public Vector3d getCameraPosition() {
        return new Vector3d(0, 0, 0);
    }

    public double getReachDistance() {
        return 5;
    }

    @Override
    public HitBox<Entity, IWorld>[] getSelectionBoxes() {
        return new HitBox[]{new HitBox<>(this.collisionBox, this, this.collisionBox.getCenter())};
    }

    @Override
    public AABB getCollisionBoxSize() {
        return new AABB(-0.3, 0, -0.3, 0.3, 1.75, 0.3);
    }

    public abstract int getHealth();

    public abstract Item[] getDrop();

    @Override
    public NBTTagCompound getData() {
        NBTTagCompound tag = super.getData();
        tag.setBoolean("flying", this.flying);
        tag.setCompoundTag("inventory", this.inventory.getData());
        return tag;
    }

    @Override
    public void setData(NBTTagCompound tag) {
        this.flying = tag.getBoolean("flying");
        this.inventory.setData(tag.getCompoundTag("inventory"));
        super.setData(tag);
    }

    public void attack() {
        if (this.hitResult != null) {
            this.hitResult.hit(this, this.world);
        }
    }

    public void interact() {
        if (this.hitResult != null) {
            this.hitResult.interact(this, world);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
