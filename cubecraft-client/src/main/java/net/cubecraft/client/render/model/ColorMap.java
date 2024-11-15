package net.cubecraft.client.render.model;

import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.access.BlockAccess;

import java.awt.image.BufferedImage;

public abstract class ColorMap {
    protected final ResourceLocation loc;
    protected BufferedImage image;

    public ColorMap(ResourceLocation loc) {
        this.loc = loc;
    }

    public void load() {
        //this.image= ClientSharedContext.RESOURCE_MANAGER.getResource(loc).getAsImage();
    }

    public int sample(BlockAccessor w, BlockAccess b) {
        if (b == null) {
            return getDummyColor();
        }

        return sample(w, b, b.getX(), b.getY(), b.getZ());
    }

    public int getDummyColor() {
        return 0xFFFFFF;
    }

    public abstract int sample(BlockAccessor w, BlockAccess blockAccess, long x, long y, long z);
}
