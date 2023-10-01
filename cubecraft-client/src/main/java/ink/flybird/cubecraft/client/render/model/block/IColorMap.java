package ink.flybird.cubecraft.client.render.model.block;

import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.cubecraft.world.IWorld;

import java.awt.image.BufferedImage;

public abstract class IColorMap {
    protected BufferedImage image;
    protected final ResourceLocation loc;
    public IColorMap(ResourceLocation loc) {
        this.loc = loc;
    }

    public void load(){
        this.image= ClientSharedContext.RESOURCE_MANAGER.getResource(loc).getAsImage();
    }

    public abstract int sample(IWorld w, IBlockAccess blockAccess, long x, long y, long z);
}
