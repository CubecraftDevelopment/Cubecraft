package ink.flybird.cubecraft.resource.item;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class ModelResource extends IResource {
    private String string;

    public ModelResource(String namespace, String relativePath) {
        super(namespace, relativePath);
    }

    public ModelResource(String all) {
        super(all);
    }

    @Override
    public void load(InputStream stream) throws Exception {
        this.string = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public String getString() {
        return this.string;
    }
}
