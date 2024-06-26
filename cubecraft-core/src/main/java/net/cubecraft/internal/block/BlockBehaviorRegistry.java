package net.cubecraft.internal.block;

import me.gb2022.commons.math.AABB;
import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.SimpleBlock;


//todo:implement block tag

/**
 * block behavior registry,auto register with field registry,
 * direct constant calls are allowed.
 *
 * @author GrassBlock2022
 */
@FieldRegistryHolder(value = "cubecraft")
public class BlockBehaviorRegistry {
    @FieldRegistry(value = "air")
    public static final Block AIR = new SimpleBlock(
            EnumFacing.all(), new AABB[]{}, new AABB[]{},
            0, 0, 0, 0, false, "cubecraft:_air", new String[]{}, 0
    );

    @FieldRegistry(value = "stone")
    public static final Block STONE = new SimpleBlock(
            EnumFacing.all(),
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            1, 1, 3, 0, true, "cubecraft:stone", new String[]{}, 0
    );

    @FieldRegistry(value = "log")
    public static final Block LOG = new SimpleBlock(
            EnumFacing.all(),
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            1, 20, 0, 0, true, "cubecraft:_log", new String[]{}, 0
    );

    @FieldRegistry(value = "log")
    public static final Block LEAVES = new SimpleBlock(
            EnumFacing.all(),
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            1, 12, 0, 110, false, "cubecraft:_leaves", new String[]{}, 0
    );

    @FieldRegistry(value = "dirt")
    public static final Block DIRT = new SimpleBlock(
            EnumFacing.all(),
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            1, 16, 1, 0, true, "cubecraft:_dirt", new String[]{}, 0
    );

    @FieldRegistry(value = "grass_block")
    public static final Block GRASS_BLOCK = new SimpleBlock(
            EnumFacing.all(),
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            1, 16, 1, 0, true, "cubecraft:_grass_block", new String[]{}, 0
    );

    @FieldRegistry(value = "glass")
    public static final Block GLASS = new SimpleBlock(
            EnumFacing.all(),
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            1, 3, 2, 127, false, "cubecraft:_glass", new String[]{}, 0
    );

    @FieldRegistry(value = "wool")
    public static final Block WOOL = new SimpleBlock(
            EnumFacing.all(),
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            1, 0, 1, 0, true, "cubecraft:_wool", new String[]{}, 0
    );

    @FieldRegistry(value = "block")
    public static final Block BLOCK = new SimpleBlock(
            EnumFacing.all(),
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            new AABB[]{new AABB(0, 0, 0, 1, 1, 1)},
            1, 0, 0, 0, true, "cubecraft:_block", new String[]{}, 0
    );

    @FieldRegistry(value = "water")
    public static final Block WATER = new SimpleBlock(
            new EnumFacing[]{EnumFacing.Up},
            new AABB[0], new AABB[0],
            1, 0.25f, 0, 127, false, "cubecraft:_water", new String[]{"cubecraft:liquid"}, 0
    );
}
