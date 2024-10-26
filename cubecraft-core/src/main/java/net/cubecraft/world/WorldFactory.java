package net.cubecraft.world;

import net.cubecraft.level.Level;

public interface WorldFactory {
    World create(String id, Level level);
}
