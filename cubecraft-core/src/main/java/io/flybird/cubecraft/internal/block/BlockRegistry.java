package io.flybird.cubecraft.internal.block;

import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;
import io.flybird.cubecraft.world.block.Block;
import io.flybird.cubecraft.world.block.OverwrittenBlock;

/**
 * block registry,auto register with field registry,
 * direct constant calls are allowed.
 *
 * @author GrassBlock2022
 */
@FieldRegistryHolder(value = "cubecraft")
public class BlockRegistry {
    //air
    @FieldRegistry(value = "air")
    public static final Block AIR = new OverwrittenBlock(BlockType.AIR, BlockBehaviorRegistry.AIR);

    //<editor-fold> stone
    @FieldRegistry(value = "stone")
    public static final Block STONE = new OverwrittenBlock(BlockType.STONE, BlockBehaviorRegistry.STONE);

    @FieldRegistry(value = "andesite")
    public static final Block ANDESITE = new OverwrittenBlock(BlockType.ANDESITE, BlockBehaviorRegistry.STONE);

    @FieldRegistry(value = "diorite")
    public static final Block DIORITE = new OverwrittenBlock(BlockType.DIORITE, BlockBehaviorRegistry.STONE);

    @FieldRegistry(value = "granite")
    public static final Block GRANITE = new OverwrittenBlock(BlockType.GRANITE, BlockBehaviorRegistry.STONE);

    @FieldRegistry(value = "granite")
    public static final Block BASALT = new OverwrittenBlock(BlockType.BASALT, BlockBehaviorRegistry.STONE);
    //</editor-fold>


    //<editor-fold> log
    @FieldRegistry(value = "oak_log")
    public static final Block OAK_LOG = new OverwrittenBlock(BlockType.OAK_LOG, BlockBehaviorRegistry.LOG);

    @FieldRegistry(value = "birch_log")
    public static final Block BIRCH_LOG = new OverwrittenBlock(BlockType.BIRCH_LOG, BlockBehaviorRegistry.LOG);

    @FieldRegistry(value = "spruce_log")
    public static final Block SPRUCE_LOG = new OverwrittenBlock(BlockType.SPRUCE_LOG, BlockBehaviorRegistry.LOG);

    @FieldRegistry(value = "mangrove_log")
    public static final Block MANGROVE_LOG = new OverwrittenBlock(BlockType.MANGROVE_LOG, BlockBehaviorRegistry.LOG);

    @FieldRegistry(value = "acacia_log")
    public static final Block ACACIA_LOG = new OverwrittenBlock(BlockType.ACACIA_LOG, BlockBehaviorRegistry.LOG);

    @FieldRegistry(value = "spruce_log")
    public static final Block DARK_OAK_LOG = new OverwrittenBlock(BlockType.DARK_OAK_LOG, BlockBehaviorRegistry.LOG);
    //</editor-fold>

    //<editor-fold> leaves
    @FieldRegistry(value = "oak_leaves")
    public static final Block OAK_LEAVES = new OverwrittenBlock(BlockType.OAK_LEAVES, BlockBehaviorRegistry.LEAVES);

    @FieldRegistry(value = "birch_leaves")
    public static final Block BIRCH_LEAVES = new OverwrittenBlock(BlockType.BIRCH_LEAVES, BlockBehaviorRegistry.LEAVES);

    @FieldRegistry(value = "spruce_leaves")
    public static final Block SPRUCE_LEAVES = new OverwrittenBlock(BlockType.SPRUCE_LEAVES, BlockBehaviorRegistry.LEAVES);

    @FieldRegistry(value = "mangrove_leaves")
    public static final Block MANGROVE_LEAVES = new OverwrittenBlock(BlockType.MANGROVE_LEAVES, BlockBehaviorRegistry.LEAVES);

    @FieldRegistry(value = "acacia_leaves")
    public static final Block ACACIA_LEAVES = new OverwrittenBlock(BlockType.ACACIA_LEAVES, BlockBehaviorRegistry.LEAVES);

    @FieldRegistry(value = "dark_oak_leaves")
    public static final Block DARK_OAK_LEAVES = new OverwrittenBlock(BlockType.DARK_OAK_LEAVES, BlockBehaviorRegistry.LEAVES);
    //</editor-fold>

    //<editor-fold> dirt
    @FieldRegistry(value = "dirt")
    public static final Block DIRT = new OverwrittenBlock(BlockType.DIRT, BlockBehaviorRegistry.DIRT);

    @FieldRegistry(value = "coarse_dirt")
    public static final Block COARSE_DIRT = new OverwrittenBlock(BlockType.COARSE_DIRT, BlockBehaviorRegistry.DIRT);
    //</editor-fold>

    //<editor-fold> grass_block
    @FieldRegistry(value = "grass_block")
    public static final Block GRASS_BLOCK = new OverwrittenBlock(BlockType.GRASS_BLOCK, BlockBehaviorRegistry.GRASS_BLOCK);

    @FieldRegistry(value = "podzol")
    public static final Block PODZOL = new OverwrittenBlock(BlockType.PODZOL, BlockBehaviorRegistry.GRASS_BLOCK);

    @FieldRegistry(value = "mycelium")
    public static final Block MYCELIUM = new OverwrittenBlock(BlockType.MYCELIUM, BlockBehaviorRegistry.GRASS_BLOCK);
    //</editor-fold>

    //<editor-fold> glass
    @FieldRegistry(value = "glass")
    public static final Block GLASS = new OverwrittenBlock(BlockType.GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "white_stained_glass")
    public static final Block WHITE_STAINED_GLASS = new OverwrittenBlock(BlockType.WHITE_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "orange_stained_glass")
    public static final Block ORANGE_STAINED_GLASS = new OverwrittenBlock(BlockType.ORANGE_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "magenta_stained_glass")
    public static final Block MAGENTA_STAINED_GLASS = new OverwrittenBlock(BlockType.MAGENTA_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "light_blue_stained_glass")
    public static final Block LIGHT_BLUE_STAINED_GLASS = new OverwrittenBlock(BlockType.LIGHT_BLUE_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "yellow_stained_glass")
    public static final Block YELLOW_STAINED_GLASS = new OverwrittenBlock(BlockType.YELLOW_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "lime_stained_glass")
    public static final Block LIME_STAINED_GLASS = new OverwrittenBlock(BlockType.LIME_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "pink_stained_glass")
    public static final Block PINK_STAINED_GLASS = new OverwrittenBlock(BlockType.PINK_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "gray_stained_glass")
    public static final Block GRAY_STAINED_GLASS = new OverwrittenBlock(BlockType.GRAY_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "light_gray_stained_glass")
    public static final Block _STAINED_GLASS = new OverwrittenBlock(BlockType.LIGHT_GRAY_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "cyan_stained_glass")
    public static final Block CYAN_STAINED_GLASS = new OverwrittenBlock(BlockType.CYAN_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "purple_stained_glass")
    public static final Block PURPLE_STAINED_GLASS = new OverwrittenBlock(BlockType.PURPLE_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "blue_stained_glass")
    public static final Block BLUE_STAINED_GLASS = new OverwrittenBlock(BlockType.BLUE_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "brown_stained_glass")
    public static final Block BROWN_STAINED_GLASS = new OverwrittenBlock(BlockType.BROWN_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "green_stained_glass")
    public static final Block GREEN_STAINED_GLASS = new OverwrittenBlock(BlockType.GREEN_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "red_stained_glass")
    public static final Block RED_STAINED_GLASS = new OverwrittenBlock(BlockType.RED_STAINED_GLASS, BlockBehaviorRegistry.GLASS);

    @FieldRegistry(value = "black_stained_glass")
    public static final Block BLACK_STAINED_GLASS = new OverwrittenBlock(BlockType.BLACK_STAINED_GLASS, BlockBehaviorRegistry.GLASS);
    //</editor-fold>

    //<editor-fold> wool
    @FieldRegistry(value = "wool")
    public static final Block WOOL = new OverwrittenBlock(BlockType.WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "orange_wool")
    public static final Block ORANGE_WOOL = new OverwrittenBlock(BlockType.ORANGE_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "magenta_wool")
    public static final Block MAGENTA_WOOL = new OverwrittenBlock(BlockType.MAGENTA_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "light_blue_wool")
    public static final Block LIGHT_BLUE_WOOL = new OverwrittenBlock(BlockType.LIGHT_BLUE_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "yellow_wool")
    public static final Block YELLOW_WOOL = new OverwrittenBlock(BlockType.YELLOW_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "lime_wool")
    public static final Block LIME_WOOL = new OverwrittenBlock(BlockType.LIME_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "pink_wool")
    public static final Block PINK_WOOL = new OverwrittenBlock(BlockType.PINK_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "gray_wool")
    public static final Block GRAY_WOOL = new OverwrittenBlock(BlockType.GRAY_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "light_gray_wool")
    public static final Block _WOOL = new OverwrittenBlock(BlockType.LIGHT_GRAY_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "cyan_wool")
    public static final Block CYAN_WOOL = new OverwrittenBlock(BlockType.CYAN_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "purple_wool")
    public static final Block PURPLE_WOOL = new OverwrittenBlock(BlockType.PURPLE_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "blue_wool")
    public static final Block BLUE_WOOL = new OverwrittenBlock(BlockType.BLUE_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "brown_wool")
    public static final Block BROWN_WOOL = new OverwrittenBlock(BlockType.BROWN_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "green_wool")
    public static final Block GREEN_WOOL = new OverwrittenBlock(BlockType.GREEN_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "red_wool")
    public static final Block RED_WOOL = new OverwrittenBlock(BlockType.RED_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "black_wool")
    public static final Block BLACK_WOOL = new OverwrittenBlock(BlockType.BLACK_WOOL, BlockBehaviorRegistry.WOOL);

    @FieldRegistry(value = "fallback")
    public static final Block UNKNOWN_BLOCK = new OverwrittenBlock("cubecraft:fallback", BlockBehaviorRegistry.STONE);
    //</editor-fold>

    @FieldRegistry(value = "calm_water")
    public static final Block CALM_WATER = new OverwrittenBlock("cubecraft:calm_water", BlockBehaviorRegistry.WATER);
}
