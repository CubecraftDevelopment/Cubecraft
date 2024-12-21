package net.cubecraft.client.render.world;

import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.ClientContext;
import net.cubecraft.client.internal.renderer.world.WorldRendererType;
import net.cubecraft.client.render.RenderType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//todo: 实体加载
@TypeItem(WorldRendererType.ENTITY)
public final class EntityRenderer extends IWorldRenderer {
    public static final Logger LOGGER = LogManager.getLogger("EntityRenderer");

    public void init() {
        this.world.getEventBus().registerEventListener(this);
        ClientContext.QUERY_HANDLER.registerCallback(this.getID(), (arg -> switch (arg) {
            default -> 0;
        }));
    }


    @Override
    public void render(RenderType type, float delta) {
        if (type != RenderType.ALPHA) {
            return;
        }

        for (var entity : this.world.getEntities().values()) {
            this.viewCamera.push().object(entity.x, entity.y, entity.z).set();

            this.viewCamera.pop();
        }
    }
}
