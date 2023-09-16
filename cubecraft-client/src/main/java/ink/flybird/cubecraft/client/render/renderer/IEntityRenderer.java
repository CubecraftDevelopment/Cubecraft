package ink.flybird.cubecraft.client.render.renderer;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.cubecraft.world.entity.Entity;

import java.util.List;

public interface IEntityRenderer {
    void render(Entity e);

    default void initializeRenderer(List<ResourceLocation> textureList){}
}
