package ink.flybird.cubecraft.resource;

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

public class Resource{
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
}
