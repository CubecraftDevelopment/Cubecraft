package ink.flybird.cubecraft.world;

import ink.flybird.cubecraft.level.Level;

public interface WorldFactory {
    IWorld create(String id, Level level);
}
