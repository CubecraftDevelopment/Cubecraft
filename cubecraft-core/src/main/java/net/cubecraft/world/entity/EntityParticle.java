package net.cubecraft.world.entity;

import me.gb2022.commons.math.AABB;
import net.cubecraft.world.IWorld;

public abstract class EntityParticle extends EntityNonLiving {
    protected final String tex;
    protected final float uo;
    protected final float vo;
    protected final int lifetime;
    protected final float size;
    protected int age;

    public EntityParticle(IWorld world, double x, double y, double z, double xa, double ya, double za, String tex) {
        super(world);
        this.tex = tex;
        this.setPos(x, y, z);
        this.xd = xa + (Math.random() * 2.0 - 1.0) * 0.4f;
        this.yd = ya + (Math.random() * 2.0 - 1.0) * 0.4f;
        this.zd = za + (Math.random() * 2.0 - 1.0) * 0.4f;
        double speed = (Math.random() + Math.random() + 1.0) * 0.15f;
        double dd = Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
        this.xd = this.xd / dd * speed * 0.4f;
        this.yd = this.yd / dd * speed * 0.4f + 0.1f;
        this.zd = this.zd / dd * speed * 0.4f;
        this.uo = (float) (Math.random() * 3.0f);
        this.vo = (float) (Math.random() * 3.0f);
        this.size = (float) (Math.random() * 0.5 + 0.5);
        this.lifetime = (int) (4.0 / (Math.random() * 0.9 + 0.1));
        this.age = 0;
    }

    @Override
    public AABB getCollisionBoxSize() {
        return new AABB(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1);
    }

    @Override
    public void tick() {
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.age++;
        this.yd = (this.yd - 0.04);
        this.move(xd, yd, zd);
        this.xd *= 0.98f;
        this.yd *= 0.98f;
        this.zd *= 0.98f;
        if (this.isOnGround()) {
            this.xd *= 0.7f;
            this.zd *= 0.7f;
        }
    }

    public int getLife() {
        return this.lifetime - this.age;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    public String getTexture() {
        return this.tex;
    }

    public float getUOffset() {
        return this.uo;
    }

    public float getVOffset() {
        return this.vo;
    }

    public int getAge() {
        return age;
    }

    public abstract float getSize();

    public abstract float getLifetime();
}
