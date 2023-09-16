package ink.flybird.cubecraft.client.resources.resource;

import java.io.InputStream;

public abstract class IResource {
    private final String relativePath;
    private final String namespace;

    protected IResource(String namespace, String relativePath) {
        this.relativePath = relativePath;
        this.namespace = namespace;
    }

    protected IResource(String all) {
        this.namespace = all.trim().split(":")[0];
        this.relativePath = all.trim().split(":")[1];

    }

    public static <T extends IResource> String getID(String namespace, String relativePath, Class<T> clazz) {
        return "%s@%s:%s".formatted(
                clazz.getSimpleName(),
                namespace,
                relativePath
        );
    }

    public static String format(String loc) {
        String namespace = loc.trim().split(":")[0];
        String relativePath = loc.trim().split(":")[1];

        return "/resource/" + namespace + relativePath;
    }

    public abstract void load(InputStream stream) throws Exception;


    public String getRelativePath() {
        return relativePath;
    }

    public String getNamespace() {
        return namespace;
    }

    public final String getAbsolutePath() {
        return this.getPathPrefix() + getRelativePath();
    }

    public String getPathPrefix() {
        return "/resource/" + this.getNamespace();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof IResource)) {
            return false;
        }
        return this.getAbsolutePath().equals(((IResource) obj).getAbsolutePath());
    }

    @Override
    public int hashCode() {
        return this.getAbsolutePath().hashCode();
    }
}
