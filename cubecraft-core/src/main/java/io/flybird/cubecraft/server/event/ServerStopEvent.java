package io.flybird.cubecraft.server.event;

import io.flybird.cubecraft.server.CubecraftServer;

public record ServerStopEvent(CubecraftServer server, String reason){
}
