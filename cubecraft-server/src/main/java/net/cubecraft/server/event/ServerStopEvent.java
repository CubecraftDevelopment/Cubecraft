package net.cubecraft.server.event;

import net.cubecraft.server.CubecraftServer;


public record ServerStopEvent(CubecraftServer server, String reason) {
}
