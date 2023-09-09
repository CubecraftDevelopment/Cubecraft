package ink.flybird.cubecraft.client.render;

public enum RenderType implements IRenderType {
    VISIBLE_AREA("visible_area"),
    ALPHA("alpha"),
    TRANSPARENT("transparent");

    final String name;

    RenderType(String name) {
        this.name = name;
    }

    public static RenderType from(String id) {
        return switch (id) {
            case "alpha" -> ALPHA;
            case "transparent" -> TRANSPARENT;
            case "visible_area" -> VISIBLE_AREA;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    public String getName() {
        return name;
    }
}
