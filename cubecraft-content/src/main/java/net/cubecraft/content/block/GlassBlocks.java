package net.cubecraft.content.block;

import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.blocks.GlassBlock;
import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;

@FieldRegistryHolder("cubecraft")
public interface GlassBlocks {
    String WHITE_STAINED_GLASS_ID = "cubecraft:white_stained_glass";
    String ORANGE_STAINED_GLASS_ID = "cubecraft:orange_stained_glass";
    String MAGENTA_STAINED_GLASS_ID = "cubecraft:magenta_stained_glass";
    String LIGHT_BLUE_STAINED_GLASS_ID = "cubecraft:light_blue_stained_glass";
    String YELLOW_STAINED_GLASS_ID = "cubecraft:yellow_stained_glass";
    String LIME_STAINED_GLASS_ID = "cubecraft:lime_stained_glass";
    String PURPLE_STAINED_GLASS_ID = "cubecraft:purple_stained_glass";
    String PINK_STAINED_GLASS_ID = "cubecraft:pink_stained_glass";
    String GRAY_STAINED_GLASS_ID = "cubecraft:gray_stained_glass";
    String BLACK_STAINED_GLASS_ID = "cubecraft:black_stained_glass";
    String LIGHT_GRAY_STAINED_GLASS_ID = "cubecraft:light_gray_stained_glass";
    String CYAN_STAINED_GLASS_ID = "cubecraft:cyan_stained_glass";
    String BLUE_STAINED_GLASS_ID = "cubecraft:blue_stained_glass";
    String BROWN_STAINED_GLASS_ID = "cubecraft:brown_stained_glass";
    String GREEN_STAINED_GLASS_ID = "cubecraft:green_stained_glass";
    String RED_STAINED_GLASS_ID = "cubecraft:red_stained_glass";

    @FieldRegistry(value = "white_stained_glass")
    Block WHITE_STAINED_GLASS = new GlassBlock(WHITE_STAINED_GLASS_ID);

    @FieldRegistry(value = "orange_stained_glass")
    Block ORANGE_STAINED_GLASS = new GlassBlock(ORANGE_STAINED_GLASS_ID);

    @FieldRegistry(value = "magenta_stained_glass")
    Block MAGENTA_STAINED_GLASS = new GlassBlock(MAGENTA_STAINED_GLASS_ID);

    @FieldRegistry(value = "light_blue_stained_glass")
    Block LIGHT_BLUE_STAINED_GLASS = new GlassBlock(LIGHT_BLUE_STAINED_GLASS_ID);

    @FieldRegistry(value = "yellow_stained_glass")
    Block YELLOW_STAINED_GLASS = new GlassBlock(YELLOW_STAINED_GLASS_ID);

    @FieldRegistry(value = "lime_stained_glass")
    Block LIME_STAINED_GLASS = new GlassBlock(LIME_STAINED_GLASS_ID);

    @FieldRegistry(value = "pink_stained_glass")
    Block PINK_STAINED_GLASS = new GlassBlock(PINK_STAINED_GLASS_ID);

    @FieldRegistry(value = "gray_stained_glass")
    Block GRAY_STAINED_GLASS = new GlassBlock(GRAY_STAINED_GLASS_ID);

    @FieldRegistry(value = "light_gray_stained_glass")
    Block LIGHT_GRAY_STAINED_GLASS = new GlassBlock(LIGHT_GRAY_STAINED_GLASS_ID);

    @FieldRegistry(value = "cyan_stained_glass")
    Block CYAN_STAINED_GLASS = new GlassBlock(CYAN_STAINED_GLASS_ID);

    @FieldRegistry(value = "purple_stained_glass")
    Block PURPLE_STAINED_GLASS = new GlassBlock(PURPLE_STAINED_GLASS_ID);

    @FieldRegistry(value = "blue_stained_glass")
    Block BLUE_STAINED_GLASS = new GlassBlock(BLUE_STAINED_GLASS_ID);

    @FieldRegistry(value = "brown_stained_glass")
    Block BROWN_STAINED_GLASS = new GlassBlock(BROWN_STAINED_GLASS_ID);

    @FieldRegistry(value = "green_stained_glass")
    Block GREEN_STAINED_GLASS = new GlassBlock(GREEN_STAINED_GLASS_ID);

    @FieldRegistry(value = "red_stained_glass")
    Block RED_STAINED_GLASS = new GlassBlock(RED_STAINED_GLASS_ID);

    @FieldRegistry(value = "black_stained_glass")
    Block BLACK_STAINED_GLASS = new GlassBlock(BLACK_STAINED_GLASS_ID);
}
