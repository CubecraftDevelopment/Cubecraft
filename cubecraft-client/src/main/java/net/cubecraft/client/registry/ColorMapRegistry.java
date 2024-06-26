package net.cubecraft.client.registry;

import net.cubecraft.client.render.model.IColorMap;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.IWorld;
import me.gb2022.commons.registry.ItemGetter;

public class ColorMapRegistry {
    @ItemGetter(id = "foliage", namespace = "cubecraft")
    public IColorMap foliage() {
        return new IColorMap(ResourceLocation.blockColorMap("cubecraft:foliage.png")) {
            @Override
            public int sample(IWorld w, IBlockAccess blockAccess, long x, long y, long z) {
                //return this.image.getRGB((int) (h*this.image.getWidth()), (int) (t/this.image.getHeight()));
                return 0x77AB2F;
            }
        };
    }

    @ItemGetter(id = "default", namespace = "cubecraft")
    public IColorMap _default() {
        return new IColorMap(ResourceLocation.blockColorMap("cubecraft:foliage.png")) {
            @Override
            public int sample(IWorld w, IBlockAccess blockAccess, long x, long y, long z) {
                return 0xFFFFFF;
            }
        };
    }

    @ItemGetter(id = "grass", namespace = "cubecraft")
    public IColorMap grass() {
        return new IColorMap(ResourceLocation.blockColorMap("cubecraft:grass.png")) {
            @Override
            public int sample(IWorld w, IBlockAccess blockAccess, long x, long y, long z) {
                // return this.image.getRGB((int) (h*this.image.getWidth()), (int) (t/this.image.getHeight()));
                return 0x91BD59;
            }
        };
    }
}
