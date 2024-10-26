package net.cubecraft.client.render.model;

import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.access.IBlockAccess;

import java.awt.image.BufferedImage;

public abstract class IColorMap {
    protected final ResourceLocation loc;
    protected BufferedImage image;

    public IColorMap(ResourceLocation loc) {
        this.loc = loc;
    }

    public void load() {
        //this.image= ClientSharedContext.RESOURCE_MANAGER.getResource(loc).getAsImage();
    }

    public abstract int sample(BlockAccessor w, IBlockAccess blockAccess, long x, long y, long z);
}
