package ink.flybird.cubecraft.client.render;

public interface WorldRenderer {
    void preRender(RenderType type, float delta);

    void render(RenderType type,float delta);

    void postRender(RenderType type, float delta);
}
