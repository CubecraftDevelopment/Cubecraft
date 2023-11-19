package net.cubecraft.world.block.blocks;

import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.property.BlockProperty;
import net.cubecraft.world.block.property.attribute.SimpleBooleanProperty;
import net.cubecraft.world.block.property.collision.BlockCollisionProperty;
import net.cubecraft.world.block.property.facing.SimpleFacingProperty;
import net.cubecraft.world.block.property.hitbox.BlockHitBoxProperty;

import java.util.Map;

public final class GlassBlock extends Block {
    public GlassBlock(String id) {
        super(id);
    }

    @Override
    public void initPropertyMap(Map<String, BlockProperty<?>> map) {
        map.put("cubecraft:collision",new BlockCollisionProperty());
        map.put("cubecraft:hitbox",new BlockHitBoxProperty());
        map.put("cubecraft:facing",new SimpleFacingProperty());
        map.put("cubecraft:solid",new SimpleBooleanProperty(false));
    }

    @Override
    public String[] getBehaviorList() {
        return new String[]{
                "cubecraft:face_top_only",
                "cubecraft:drop_with_silk_touch_only"
        };
    }
}
