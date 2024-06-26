package net.cubecraft.client.render.model;

import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.IWorld;

import java.awt.image.BufferedImage;

public abstract class IColorMap {
    protected BufferedImage image;
    protected final ResourceLocation loc;
    public IColorMap(ResourceLocation loc) {
        this.loc = loc;
    }

    public void load(){
        //this.image= ClientSharedContext.RESOURCE_MANAGER.getResource(loc).getAsImage();
    }

    public abstract int sample(IWorld w, IBlockAccess blockAccess, long x, long y, long z);
}
