package net.cubecraft.internal.entity;

import net.cubecraft.auth.Session;
import net.cubecraft.SharedContext;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.entity.EntityItem;
import net.cubecraft.world.entity.EntityLiving;
import ink.flybird.fcommon.registry.TypeItem;
import org.joml.Vector3d;

@TypeItem("cubecraft:player")
public class EntityPlayer extends EntityLiving {
    private final Session session;

    public EntityPlayer(IWorld world, Session session) {
        super(world);
        this.session = session;
        this.setUuid(SharedContext.SESSION_SERVICE.get(session.getType()).genUUID(session));
        this.flying =true;
    }

    @Override
    public EntityItem[] getDrop() {
        return new EntityItem[0];
    }

    @Override
    public double getReachDistance() {
        return 5;
    }

    @Override
    public int getHealth() {
        return 20;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public Vector3d getCameraPosition() {
        return new Vector3d(0,1.62,0);
    }
}
