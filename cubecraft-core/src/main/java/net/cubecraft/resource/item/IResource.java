package net.cubecraft.resource.item;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public abstract class IResource {
    private final String relativePath;
    private final String namespace;
    private byte[] data;

    protected IResource(String namespace, String relativePath) {
        this.relativePath = relativePath;
        this.namespace = namespace;
    }

    protected IResource(String all) {
        this.namespace = all.trim().split(":")[0];
        this.relativePath = all.trim().split(":")[1];

    }

    public void loadResource(InputStream stream) throws Exception {
        this.data=stream.readAllBytes();
        this.load(this.getStream());
    }

    public abstract void load(InputStream stream) throws Exception;


    public String getRelativePath() {
        return relativePath;
    }

    public String getNamespace() {
        return namespace;
    }

    public final String getAbsolutePath() {
        return this.formatPath(this.getNamespace(), this.getRelativePath());
    }

    public abstract String formatPath(String namespace, String relPath);


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof IResource res)) {
            return false;
        }
        return this.getAbsolutePath().equals(res.getAbsolutePath());
    }

    @Override
    public int hashCode() {
        return this.getAbsolutePath().hashCode();
    }



    public byte[] getData() {
        return data;
    }

    public InputStream getStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public String toString() {
        return getAbsolutePath();
    }
}
