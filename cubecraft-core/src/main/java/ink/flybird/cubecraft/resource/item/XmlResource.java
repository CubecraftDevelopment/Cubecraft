package ink.flybird.cubecraft.resource.item;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public abstract class XmlResource extends IResource {
    public static final DocumentBuilder DOM_PARSER;

    static {
        try {
            DOM_PARSER = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private Document dom;

    public XmlResource(String namespace, String relativePath) {
        super(namespace, relativePath);
    }

    public XmlResource(String all) {
        super(all);
    }

    public Document getDom() {
        return dom;
    }

    @Override
    public void load(InputStream stream) throws Exception {
        try {
            this.dom = DOM_PARSER.parse(stream);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
