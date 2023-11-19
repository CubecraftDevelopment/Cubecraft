package net.cubecraft.world.block.blocks;

import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;
import net.cubecraft.internal.block.BlockBehaviorRegistry;
import net.cubecraft.internal.block.BlockType;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.OverwrittenBlock;
import net.cubecraft.world.block.property.BlockProperty;
import net.cubecraft.world.block.property.attribute.SimpleBooleanProperty;

import java.util.Map;

/**
 * block registry,auto register with field registry,
 * direct constant calls are allowed.
 *
 * @author GrassBlock2022
 */
@FieldRegistryHolder(value = "cubecraft")
public interface BlockRegistry {
    //base blocks
    @FieldRegistry(value = "air")
    Block AIR = new BlockAir();

    @FieldRegistry(value = "stone")
    Block STONE = new StoneBlock("cubecraft:stone");

    @FieldRegistry(value = "oak_leaves")
    Block OAK_LEAVES = new OverwrittenBlock(BlockType.OAK_LEAVES, BlockBehaviorRegistry.LEAVES);

    @FieldRegistry(value = "oak_log")
    Block OAK_LOG = new OverwrittenBlock(BlockType.OAK_LOG, BlockBehaviorRegistry.LOG);

    @FieldRegistry(value = "dirt")
    Block DIRT = new DirtBlock(BlockType.DIRT);

    @FieldRegistry(value = "grass_block")
    Block GRASS_BLOCK = new GrassBlock(BlockType.GRASS_BLOCK);

    @FieldRegistry(value = "calm_water")
    Block CALM_WATER = new LiquidBlock("cubecraft:calm_water");


    //extra blocks
    @FieldRegistry(value = "coarse_dirt")
    Block COARSE_DIRT = new DirtBlock(BlockType.COARSE_DIRT);
    //</editor-fold>

    //<editor-fold> grass_block


    @FieldRegistry(value = "podzol")
    Block PODZOL = new GrassBlock(BlockType.PODZOL);

    @FieldRegistry(value = "mycelium")
    Block MYCELIUM = new GrassBlock(BlockType.MYCELIUM);


    @FieldRegistry(value = "glass")
    Block GLASS = new OverwrittenBlock(BlockType.GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "wool")
    Block WOOL = new OverwrittenBlock(BlockType.WOOL, BlockBehaviorRegistry.WOOL);


    @FieldRegistry(value = "blue_stained_glass")
    Block BLUE_STAINED_GLASS = new GlassBlock("cubecraft:blue_stained_glass");


    @FieldRegistry(value = "fallback")
    Block UNKNOWN_BLOCK = new OverwrittenBlock("cubecraft:fallback", BlockBehaviorRegistry.STONE);





    class BlockAir extends Block {
        public BlockAir() {
            super("cubecraft:air");
        }

        @Override
        public void initPropertyMap(Map<String, BlockProperty<?>> map) {
            map.put("cubecraft:solid",new SimpleBooleanProperty(false));
        }

        @Override
        public String[] getBehaviorList() {
            return new String[0];
        }
    }
}
