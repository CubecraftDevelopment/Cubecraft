package net.cubecraft.client.render.chunk;

public enum ChunkUpdateStatus {
    UNCHECKED("unchecked"),
    CHECKED("checked"),
    UPDATE_FAILED("failed");

    private final String id;

    ChunkUpdateStatus(String id) {
        this.id = id;
    }

    public static ChunkUpdateStatus fromId(String id) {
        return switch (id) {
            case "unchecked" -> UNCHECKED;
            case "checked" -> CHECKED;
            case "failed" -> UPDATE_FAILED;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    public String getId() {
        return id;
    }
}
