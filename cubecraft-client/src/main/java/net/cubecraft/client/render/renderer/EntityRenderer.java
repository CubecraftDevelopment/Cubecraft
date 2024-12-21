package net.cubecraft.client.render.renderer;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.world.entity.Entity;

import java.util.List;

public interface EntityRenderer {
    void render(Entity e);

    default void initializeRenderer(List<ResourceLocation> textureList){}
}
