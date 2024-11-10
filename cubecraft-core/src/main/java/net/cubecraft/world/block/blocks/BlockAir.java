package net.cubecraft.world.block.blocks;

import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.property.BlockProperty;
import net.cubecraft.world.block.property.BooleanProperty;
import net.cubecraft.world.block.property.IntProperty;
import net.cubecraft.world.block.property.collision.EmptyCollisionProperty;
import net.cubecraft.world.block.property.hitbox.EmptyHitboxProperty;

import java.util.Map;

public final class BlockAir extends Block {
    public BlockAir() {
        super("cubecraft:air");
    }

    @Override
    public void initPropertyMap(Map<String, BlockProperty<?>> map) {
        map.put("cubecraft:solid", new BooleanProperty(false));
        map.put("cubecraft:opacity", new IntProperty(127));
        map.put("cubecraft:collision", new EmptyCollisionProperty());
        map.put("cubecraft:selection", new EmptyHitboxProperty());
    }

    @Override
    public String[] getBehaviorList() {
        return new String[0];
    }
}
