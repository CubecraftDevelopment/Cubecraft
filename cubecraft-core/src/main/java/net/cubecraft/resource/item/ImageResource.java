package net.cubecraft.resource.item;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public abstract class ImageResource extends IResource {
    protected BufferedImage image;

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

    public BufferedImage getImage() {
        return image;
    }
}
