package ink.flybird.cubecraft.client.resources.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public final class RawResource extends IResource {
    private byte[] data;

    public RawResource(String namespace, String relativePath) {
        super(namespace, relativePath);
    }

    public RawResource(String all) {
        super(all);
    }

    @Override
    public void load(InputStream stream) throws Exception {
        this.data = stream.readAllBytes();
    }

    @Override
    public String getPathPrefix() {
        return "/resource/" + this.getNamespace() + "/text";
    }

    public byte[] getData() {
        return data;
    }

    public InputStream getStream() {
        return new ByteArrayInputStream(data);
    }
}
