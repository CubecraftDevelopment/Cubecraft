package ink.flybird.cubecraft.client.resources;

import java.io.IOException;

public class ResourceLoadException extends RuntimeException {
    public ResourceLoadException(IOException e) {
        super(e);
    }
}
