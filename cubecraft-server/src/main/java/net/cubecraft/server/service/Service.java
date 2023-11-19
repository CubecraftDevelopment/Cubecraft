package net.cubecraft.server.service;

import net.cubecraft.server.CubecraftServer;

public interface Service {
    default void initialize(CubecraftServer server) {
    }

    default void postInitialize(CubecraftServer server){

    }

    default void stop(CubecraftServer server) {
    }

    default void postStop(CubecraftServer server) {
    }

    default void onServerTick(CubecraftServer server) {
    }
}
