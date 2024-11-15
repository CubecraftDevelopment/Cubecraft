package net.cubecraft.resource.item;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class XmlResource extends IResource {
    public static final Map<Thread, DocumentBuilder> DOM_PARSER = new ConcurrentHashMap<>();

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
        this.dom = DOM_PARSER.computeIfAbsent(Thread.currentThread(), (k) -> {
            try {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
        }).parse(stream);
    }
}
