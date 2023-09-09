package io.flybird.cubecraft.event;

import io.flybird.cubecraft.world.block.Block;

@Deprecated
public record BlockRegisterEvent(String id, Block block) {
}
