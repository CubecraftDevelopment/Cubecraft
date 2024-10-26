package net.cubecraft.world.worldGen.structure.shapes;

import net.cubecraft.util.WorldRandom;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.blocks.Blocks;
import net.cubecraft.world.worldGen.structure.StructureGeneratingContainer;
import net.cubecraft.world.worldGen.structure.StructureShape;

public final class SmallTree implements StructureShape {
    private final Registered<Block> log;
    private final Registered<Block> leaves;

    public SmallTree(Registered<Block> log, Registered<Block> leaves) {
        this.log = log;
        this.leaves = leaves;
    }

    //todo: mc styled random culling
    @Override
    public void generate(StructureGeneratingContainer container) {
        int leavesHeight = (int) (3 + WorldRandom.xz(container.getBaseX(), container.getBaseZ()) * 2);

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                container.setBlock(x, leavesHeight, z, this.leaves);
                container.setBlock(x, leavesHeight + 1, z, this.leaves);
            }
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                container.setBlock(x, leavesHeight + 2, z, this.leaves);
                container.setBlock(x, leavesHeight + 3, z, this.leaves);
            }
        }

        for (int h = leavesHeight; h < leavesHeight + 1; h++) {
            container.setBlock(-2, h, -2, Blocks.AIR);
            container.setBlock(-2, h, 2, Blocks.AIR);
            container.setBlock(2, h, -2, Blocks.AIR);
            container.setBlock(2, h, 2, Blocks.AIR);
        }

        for (int h = leavesHeight + 2; h <= leavesHeight + 3; h++) {
            container.setBlock(-1, h, -1, Blocks.AIR);
            container.setBlock(-1, h, 1, Blocks.AIR);
            container.setBlock(1, h, -1, Blocks.AIR);
            container.setBlock(1, h, 1, Blocks.AIR);
        }

        for (int i = 0; i < leavesHeight + 2; i++) {
            container.setBlock(0, i, 0, this.log);
        }
    }

    @Override
    public int radius() {
        return 2;
    }

    @Override
    public int topBound() {
        return 9;
    }
}
