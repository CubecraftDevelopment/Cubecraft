package ink.flybird.cubecraft.client.resources.resource;

import ink.flybird.quantum3d.textures.ITextureImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public final class ImageResource extends IResource implements ITextureImage {
    private BufferedImage image;

    public ImageResource(String namespace, String relativePath) {
        super(namespace, relativePath);
    }

    public ImageResource(String s) {
        super(s);
    }

    @Override
    public void load(InputStream stream) throws Exception {
        this.image = ImageIO.read(stream);
    }

    @Override
    public String getPathPrefix() {
        return "/resource/" + this.getNamespace() + "/texture";
    }

    @Override
    public BufferedImage getAsImage() {
        return this.image;
    }

    @Override
    public String getName() {
        return this.getAbsolutePath();
    }
}
