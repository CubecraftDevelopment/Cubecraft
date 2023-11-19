package net.cubecraft.client.render.world;

import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.internal.renderer.world.WorldRendererType;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.IWorld;

import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.quantum3d_legacy.Camera;

//todo: 实体加载
@TypeItem(WorldRendererType.ENTITY)
public final class EntityRenderer extends IWorldRenderer {
    private final ILogger logger = LogManager.getLogger("entity-renderer");

    public EntityRenderer(Window window, IWorld world, EntityPlayer player, Camera cam) {
        super(window, world, player, cam);
    }

    public void init() {
        this.world.getEventBus().registerEventListener(this);
        ClientSharedContext.QUERY_HANDLER.registerCallback(this.getID(), (arg -> switch (arg) {
            default -> 0;
        }));
    }
}
