package ink.flybird.cubecraft.server.event;

import ink.flybird.cubecraft.server.CubecraftServer;

public record ServerStopEvent(CubecraftServer server, String reason){
}
