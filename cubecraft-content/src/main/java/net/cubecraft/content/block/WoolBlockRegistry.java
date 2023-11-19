package net.cubecraft.content.block;

import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.blocks.WoolBlock;
import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;

@FieldRegistryHolder("cubecraft")
public interface WoolBlockRegistry {
    String WHITE_STAINED_WOOL_ID = "cubecraft:white_wool";
    String ORANGE_STAINED_WOOL_ID = "cubecraft:orange_wool";
    String MAGENTA_STAINED_WOOL_ID = "cubecraft:magenta_wool";
    String LIGHT_BLUE_STAINED_WOOL_ID = "cubecraft:light_blue_wool";
    String YELLOW_STAINED_WOOL_ID = "cubecraft:yellow_wool";
    String LIME_STAINED_WOOL_ID = "cubecraft:lime_wool";
    String PURPLE_STAINED_WOOL_ID = "cubecraft:purple_wool";
    String PINK_STAINED_WOOL_ID = "cubecraft:pink_wool";
    String GRAY_STAINED_WOOL_ID = "cubecraft:gray_wool";
    String BLACK_STAINED_WOOL_ID = "cubecraft:black_wool";
    String LIGHT_GRAY_STAINED_WOOL_ID = "cubecraft:light_gray_wool";
    String CYAN_STAINED_WOOL_ID = "cubecraft:cyan_wool";
    String BLUE_STAINED_WOOL_ID = "cubecraft:blue_wool";
    String BROWN_STAINED_WOOL_ID = "cubecraft:brown_wool";
    String GREEN_STAINED_WOOL_ID = "cubecraft:green_wool";
    String RED_STAINED_WOOL_ID = "cubecraft:red_wool";

    @FieldRegistry(value = "white_wool")
    Block WHITE_STAINED_WOOL = new WoolBlock(WHITE_STAINED_WOOL_ID);

    @FieldRegistry(value = "orange_wool")
    Block ORANGE_STAINED_WOOL = new WoolBlock(ORANGE_STAINED_WOOL_ID);

    @FieldRegistry(value = "magenta_wool")
    Block MAGENTA_STAINED_WOOL = new WoolBlock(MAGENTA_STAINED_WOOL_ID);

    @FieldRegistry(value = "light_blue_wool")
    Block LIGHT_BLUE_STAINED_WOOL = new WoolBlock(LIGHT_BLUE_STAINED_WOOL_ID);

    @FieldRegistry(value = "yellow_wool")
    Block YELLOW_STAINED_WOOL = new WoolBlock(YELLOW_STAINED_WOOL_ID);

    @FieldRegistry(value = "lime_wool")
    Block LIME_STAINED_WOOL = new WoolBlock(LIME_STAINED_WOOL_ID);

    @FieldRegistry(value = "pink_wool")
    Block PINK_STAINED_WOOL = new WoolBlock(PINK_STAINED_WOOL_ID);

    @FieldRegistry(value = "gray_wool")
    Block GRAY_STAINED_WOOL = new WoolBlock(GRAY_STAINED_WOOL_ID);

    @FieldRegistry(value = "light_gray_wool")
    Block LIGHT_GRAY_STAINED_WOOL = new WoolBlock(LIGHT_GRAY_STAINED_WOOL_ID);

    @FieldRegistry(value = "cyan_wool")
    Block CYAN_STAINED_WOOL = new WoolBlock(CYAN_STAINED_WOOL_ID);

    @FieldRegistry(value = "purple_wool")
    Block PURPLE_STAINED_WOOL = new WoolBlock(PURPLE_STAINED_WOOL_ID);

    @FieldRegistry(value = "blue_wool")
    Block BLUE_STAINED_WOOL = new WoolBlock(BLUE_STAINED_WOOL_ID);

    @FieldRegistry(value = "brown_wool")
    Block BROWN_STAINED_WOOL = new WoolBlock(BROWN_STAINED_WOOL_ID);

    @FieldRegistry(value = "green_wool")
    Block GREEN_STAINED_WOOL = new WoolBlock(GREEN_STAINED_WOOL_ID);

    @FieldRegistry(value = "red_wool")
    Block RED_STAINED_WOOL = new WoolBlock(RED_STAINED_WOOL_ID);

    @FieldRegistry(value = "black_wool")
    Block BLACK_STAINED_WOOL = new WoolBlock(BLACK_STAINED_WOOL_ID);
}
