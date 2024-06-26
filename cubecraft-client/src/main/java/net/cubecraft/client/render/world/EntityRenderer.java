package net.cubecraft.client.render.world;

import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.internal.renderer.world.WorldRendererType;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.IWorld;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.device.Window;
import ink.flybird.quantum3d_legacy.Camera;

//todo: 实体加载
@TypeItem(WorldRendererType.ENTITY)
public final class EntityRenderer extends IWorldRenderer {
    private final Logger logger = LogManager.getLogger("entity-renderer");

    public void init() {
        this.world.getEventBus().registerEventListener(this);
        ClientSharedContext.QUERY_HANDLER.registerCallback(this.getID(), (arg -> switch (arg) {
            default -> 0;
        }));
    }

    @Override
    public void render(RenderType type, float delta) {

    }
}
