package net.cubecraft;

import java.util.List;

public enum Side {
    CLIENT,
    SERVER,
    SHARED;

    public static Side fromString(String id) {
        return switch (id) {
            case "client" -> Side.CLIENT;
            case "server" -> Side.SERVER;
            case "both", "shared" -> Side.SHARED;
            default -> throw new RuntimeException("wtf?");
        };
    }

    public boolean contains(Side side) {
        if (this == SHARED) {
            return false;
        }
        if (this == SERVER) {
            return side == SHARED;
        }
        return true;
    }

    public List<Side> getContained() {
        if (this == CLIENT) {
            return List.of(CLIENT, SERVER, SHARED);
        }
        if (this == SERVER) {
            return List.of(SERVER, SHARED);
        }
        return List.of(SHARED);
    }
}
