package net.cubecraft.world;

import net.cubecraft.level.Level;

public interface WorldFactory {
    IWorld create(String id, Level level);
}
