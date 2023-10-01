package ink.flybird.cubecraft.client.render.world;

import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.internal.renderer.world.WorldRendererType;
import ink.flybird.cubecraft.client.render.RenderType;
import ink.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.quantum3d_legacy.Camera;

//todo: 实体加载，blockmap
@TypeItem(WorldRendererType.ENTITY)
public final class EntityRenderer extends IWorldRenderer {
    private final Logger logger = new SimpleLogger("EntityRenderer");

    public EntityRenderer(Window window, IWorld world, EntityPlayer player, Camera cam, GameSetting setting) {
        super(window, world, player, cam, setting);
    }

    public void init() {
        this.world.getEventBus().registerEventListener(this);
        ClientSharedContext.QUERY_HANDLER.registerCallback(this.getID(), (arg -> switch (arg) {
            default -> 0;
        }));
    }
}
