package net.cubecraft.event.register;

import net.cubecraft.world.block.Block;

public final class BlockRegisterEvent implements RegisterEvent<Block> {
    private final String id;
    private final Block block;

    public BlockRegisterEvent(String id, Block block) {
        this.id = id;
        this.block = block;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Block getObject() {
        return this.block;
    }
}
