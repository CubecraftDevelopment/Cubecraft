package net.cubecraft.client.render.world;

import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.internal.renderer.world.WorldRendererType;
import net.cubecraft.client.render.RenderType;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import me.gb2022.commons.registry.TypeItem;

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
