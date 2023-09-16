package ink.flybird.cubecraft.event;

import ink.flybird.cubecraft.world.block.Block;

@Deprecated
public record BlockRegisterEvent(String id, Block block) {
}
