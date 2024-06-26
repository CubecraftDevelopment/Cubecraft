package net.cubecraft.content.block;

import me.gb2022.commons.registry.FieldRegistry;
import net.cubecraft.internal.block.BlockBehaviorRegistry;
import net.cubecraft.internal.block.BlockType;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.OverwrittenBlock;

public interface TreeBlocks {

    @FieldRegistry(value = "birch_log")
    Block BIRCH_LOG = new OverwrittenBlock(BlockType.BIRCH_LOG, BlockBehaviorRegistry.LOG);

    @FieldRegistry(value = "spruce_log")
    Block SPRUCE_LOG = new OverwrittenBlock(BlockType.SPRUCE_LOG, BlockBehaviorRegistry.LOG);

    @FieldRegistry(value = "mangrove_log")
    Block MANGROVE_LOG = new OverwrittenBlock(BlockType.MANGROVE_LOG, BlockBehaviorRegistry.LOG);

    @FieldRegistry(value = "acacia_log")
    Block ACACIA_LOG = new OverwrittenBlock(BlockType.ACACIA_LOG, BlockBehaviorRegistry.LOG);

    @FieldRegistry(value = "spruce_log")
    Block DARK_OAK_LOG = new OverwrittenBlock(BlockType.DARK_OAK_LOG, BlockBehaviorRegistry.LOG);

    @FieldRegistry(value = "birch_leaves")
    Block BIRCH_LEAVES = new OverwrittenBlock(BlockType.BIRCH_LEAVES, BlockBehaviorRegistry.LEAVES);

    @FieldRegistry(value = "spruce_leaves")
    Block SPRUCE_LEAVES = new OverwrittenBlock(BlockType.SPRUCE_LEAVES, BlockBehaviorRegistry.LEAVES);

    @FieldRegistry(value = "mangrove_leaves")
    Block MANGROVE_LEAVES = new OverwrittenBlock(BlockType.MANGROVE_LEAVES, BlockBehaviorRegistry.LEAVES);

    @FieldRegistry(value = "acacia_leaves")
    Block ACACIA_LEAVES = new OverwrittenBlock(BlockType.ACACIA_LEAVES, BlockBehaviorRegistry.LEAVES);

    @FieldRegistry(value = "dark_oak_leaves")
    Block DARK_OAK_LEAVES = new OverwrittenBlock(BlockType.DARK_OAK_LEAVES, BlockBehaviorRegistry.LEAVES);
}
