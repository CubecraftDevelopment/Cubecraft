package ink.flybird.quantum3d.textures;


public class TextureManager {
    private final TextureContainer<Texture2D> texture2DContainer = new TextureContainer<>();
    private final TextureContainer<Texture2DTileMap> texture2DTileMapContainer = new TextureContainer<>();

    public TextureContainer<Texture2D> getTexture2DContainer() {
        return texture2DContainer;
    }

    public TextureContainer<Texture2DTileMap> getTexture2DTileMapContainer() {
        return texture2DTileMapContainer;
    }

    public void createTexture2D(ITextureImage image, boolean ms, boolean mip) {
        Texture2D texture = new Texture2D(ms, mip);
        texture.generateTexture();
        texture.load(image);
        this.getTexture2DContainer().set(image.getName(), texture);
    }
}
