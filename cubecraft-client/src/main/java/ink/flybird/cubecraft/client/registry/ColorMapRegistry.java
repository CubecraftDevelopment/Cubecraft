package ink.flybird.cubecraft.client.registry;

import ink.flybird.cubecraft.client.render.model.block.IColorMap;
import ink.flybird.cubecraft.resource.ResourceLocation;
import ink.flybird.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.fcommon.registry.ItemGetter;

public class ColorMapRegistry {
    @ItemGetter(id = "foliage", namespace = "cubecraft")
    public IColorMap foliage() {
        return new IColorMap(ResourceLocation.blockColorMap("cubecraft:foliage.png")) {
            @Override
            public int sample(IWorld w, IBlockAccess blockAccess, long x, long y, long z) {
                double t = blockAccess.getTemperature();
                double h = blockAccess.getHumidity();
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
                double t = blockAccess.getTemperature();
                double h = blockAccess.getHumidity();
                // return this.image.getRGB((int) (h*this.image.getWidth()), (int) (t/this.image.getHeight()));
                return 0x91BD59;
            }
        };
    }
}
