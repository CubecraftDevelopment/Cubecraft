package net.cubecraft.client.render.model.block;

public enum CullingMethod {
    EQUALS,
    ALWAYS,
    NEVER,
    SOLID_OR_EQUALS,
    SOLID;

    public static CullingMethod from(String id) {
        return switch (id) {
            case "solid" -> SOLID;
            case "equals" -> EQUALS;
            case "never" -> NEVER;
            case "always" -> ALWAYS;
            case "solid_or_equals" -> SOLID_OR_EQUALS;
            default -> throw new IllegalArgumentException("unknown value:" + id);
        };
    }
}
