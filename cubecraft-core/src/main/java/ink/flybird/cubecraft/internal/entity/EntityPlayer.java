package ink.flybird.cubecraft.internal.entity;

import ink.flybird.cubecraft.auth.Session;
import ink.flybird.cubecraft.register.SharedContext;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.entity.item.Item;
import ink.flybird.cubecraft.world.entity.EntityLiving;
import ink.flybird.fcommon.registry.TypeItem;
import org.joml.Vector3d;

@TypeItem("cubecraft:player")
public class EntityPlayer extends EntityLiving {
    private final Session session;

    public EntityPlayer(IWorld world, Session session) {
        super(world);
        this.session = session;
        this.uuid= SharedContext.SESSION_SERVICE.get(session.getType()).genUUID(session);
        this.flying =true;
    }

    @Override
    public Item[] getDrop() {
        return new Item[0];
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
