package net.cubecraft.client.registry;

import net.cubecraft.client.render.model.ColorMap;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.access.BlockAccess;
import me.gb2022.commons.registry.ItemGetter;

public class ColorMapRegistry {











    @ItemGetter(id = "foliage", namespace = "cubecraft")
    public static ColorMap foliage() {
        return new ColorMap(ResourceLocation.blockColorMap("cubecraft:foliage.png")) {
            @Override
            public int sample(BlockAccessor w, BlockAccess blockAccess, long x, long y, long z) {
                //return this.image.getRGB((int) (h*this.image.getWidth()), (int) (t/this.image.getHeight()));
                return 0x77AB2F;
            }
        };
    }

    @ItemGetter(id = "default", namespace = "cubecraft")
    public ColorMap _default() {
        return new ColorMap(ResourceLocation.blockColorMap("cubecraft:foliage.png")) {
            @Override
            public int sample(BlockAccessor w, BlockAccess blockAccess, long x, long y, long z) {
                return 0xFFFFFF;
            }
        };
    }

    @ItemGetter(id = "grass", namespace = "cubecraft")
    public ColorMap grass() {
        return new ColorMap(ResourceLocation.blockColorMap("cubecraft:grass.png")) {
            @Override
            public int sample(BlockAccessor w, BlockAccess blockAccess, long x, long y, long z) {
                // return this.image.getRGB((int) (h*this.image.getWidth()), (int) (t/this.image.getHeight()));
                return 0x91BD59;
            }
        };
    }
}
