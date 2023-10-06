package ink.flybird.cubecraft.server.service;

import ink.flybird.cubecraft.server.CubecraftServer;

public interface Service {
    default void initialize(CubecraftServer server) {
    }

    default void stop(CubecraftServer server) {
    }

    default void onServerTick(CubecraftServer server) {
    }
}
