package ink.flybird.cubecraft.client.resources;

import ink.flybird.quantum3d.textures.ITextureImage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Resource implements ITextureImage {
    private final byte[] data;
    private final String name;

    public Resource(String name, InputStream in) {
        try {
            this.data = in.readAllBytes();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.name = name;
    }

    @Override
    public BufferedImage getAsImage() {
        InputStream in = new ByteArrayInputStream(this.data);
        BufferedImage image;
        try {
            image = ImageIO.read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getAsText() {
        return new String(this.data);
    }

    public Document getAsDom() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(this.getAsText().getBytes()));
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getAsStream() {
        return new ByteArrayInputStream(this.data);
    }

    public Font getAsFont() {
        try {
            return Font.createFont(Font.TRUETYPE_FONT,this.getAsStream());
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
