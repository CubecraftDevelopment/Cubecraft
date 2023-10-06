package ink.flybird.cubecraft.client.render.gui;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.render.renderer.ComponentRendererPart;
import ink.flybird.cubecraft.client.resource.TextureAsset;
import ink.flybird.quantum3d_legacy.textures.Texture2D;

import java.util.Set;

public abstract class ImageComponentRendererPart implements ComponentRendererPart {
    protected final double x0;
    protected final double x1;
    protected final double y0;
    protected final double y1;
    protected final String textureLocation;

    public ImageComponentRendererPart(double x0, double x1, double y0, double y1, String textureLocation) {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
        this.textureLocation = textureLocation;
    }

    @Override
    public void initializeRenderer(Set<TextureAsset> loc) {
        loc.add(new TextureAsset(this.textureLocation));
    }

    public Texture2D getTexture(){
        return ClientRenderContext.TEXTURE.getTexture2DContainer().get(TextureAsset.format(this.textureLocation));
    }
}
