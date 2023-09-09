package ink.flybird.cubecraft.client.render.model.block;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import io.flybird.cubecraft.world.block.IBlockAccess;
import io.flybird.cubecraft.world.IWorld;

import java.awt.image.BufferedImage;

public abstract class IColorMap {
    protected BufferedImage image;
    protected final ResourceLocation loc;
    public IColorMap(ResourceLocation loc) {
        this.loc = loc;
    }

    public void load(){
        this.image= ClientRenderContext.RESOURCE_MANAGER.getResource(loc).getAsImage();
    }

    public abstract int sample(IWorld w, IBlockAccess blockAccess, long x, long y, long z);
}
