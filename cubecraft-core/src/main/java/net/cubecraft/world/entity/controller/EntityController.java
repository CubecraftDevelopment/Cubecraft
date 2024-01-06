package net.cubecraft.world.entity.controller;

import net.cubecraft.world.entity.EntityLiving;

public abstract class EntityController<E extends EntityLiving> {
    private E entity;
    private int xAction = 0;
    private int zAction = 0;

    public void handle(E entity) {
        this.entity = entity;
    }

    public E detach() {
        E e = this.entity;
        this.entity = null;
        return e;
    }

    public E getEntity() {
        return this.entity;
    }


    //move
    public void moveForward() {
        this.zAction = Math.max(this.zAction - 1, -1);
    }

    public void moveBackward() {
        this.zAction = Math.min(this.zAction + 1, 1);
    }

    public void moveLeft() {
        this.xAction = Math.min(this.xAction - 1, -1);
    }

    public void moveRight() {
        this.xAction = Math.min(this.xAction + 1, 1);
    }

    public void jump() {
        if (this.entity.inLiquid()) {
            if(this.entity.yd<0.15f) {
                this.entity.yd += 0.05f;
            }
        } else if (this.entity.isOnGround()) {
            this.entity.yd = 0.45f;
        }
    }

    //fly
    public void toggleFly() {
        this.entity.setFlying(!this.entity.isFlying());
    }

    public void flyUp() {
        entity.yd = 0.45f;
    }

    public void flyDown() {
        entity.yd = -0.45f;
    }

    //mode
    public void toggleSprint() {
        this.entity.setSprinting(!this.entity.isSprinting());
    }

    public void tick() {
        this.entity.moveRelative(this.xAction, this.zAction);
        this.xAction = 0;
        this.zAction = 0;
    }

    protected boolean isHandlingEntity() {
        return this.entity != null;
    }
}
