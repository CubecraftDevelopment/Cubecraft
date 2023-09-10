package ink.flybird.cubecraft.client.resources.resource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class TextResource extends IResource {
    private String string;

    public TextResource(String namespace, String relativePath) {
        super(namespace, relativePath);
    }

    public TextResource(String all) {
        super(all);
    }

    @Override
    public void load(InputStream stream) throws Exception {
        this.string = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }

    @Override
    public String getPathPrefix() {
        return "/resource/" + this.getNamespace() + "/text";
    }

    public String getString() {
        return this.string;
    }
}