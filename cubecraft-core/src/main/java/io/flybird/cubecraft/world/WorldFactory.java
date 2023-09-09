package io.flybird.cubecraft.world;

import io.flybird.cubecraft.level.Level;

public interface WorldFactory {
    IWorld create(String id, Level level);
}
