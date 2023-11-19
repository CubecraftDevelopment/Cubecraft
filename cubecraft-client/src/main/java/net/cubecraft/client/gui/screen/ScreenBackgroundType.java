package net.cubecraft.client.gui.screen;

public enum ScreenBackgroundType {
    EMPTY(false),
    IN_GAME(true),
    IN_GAME_MASK(true),
    IMAGE_BACKGROUND(false),
    IMAGE_BLUR_BACKGROUND(false),
    TILE_BACKGROUND(false),
    IMAGE_BLUR_MASK_BACKGROUND(false);

    final boolean renderWorld;

    ScreenBackgroundType(boolean renderWorld) {
        this.renderWorld = renderWorld;
    }

    public static ScreenBackgroundType from(String id) {
        return switch (id) {
            case "in-game" -> IN_GAME;
            case "in-game-mask" -> IN_GAME_MASK;
            case "image-bg" -> IMAGE_BACKGROUND;
            case "tile-bg" -> TILE_BACKGROUND;
            case "image-bg-blur" -> IMAGE_BLUR_BACKGROUND;
            case "image-bg-blur-mask" -> IMAGE_BLUR_MASK_BACKGROUND;
            case "empty" -> EMPTY;
            default -> throw new IllegalArgumentException("no matched constant named %s".formatted(id));
        };
    }

    public boolean shouldRenderWorld() {
        return renderWorld;
    }
}
