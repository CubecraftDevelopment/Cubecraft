package net.cubecraft.resource.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.InputStream;

public abstract class JsonResource extends IResource {
    private JsonElement element;
    private String rawText;

    public JsonResource(String namespace, String relativePath) {
        super(namespace, relativePath);
    }

    public JsonResource(String all) {
        super(all);
    }

    public JsonElement getElement() {
        return element;
    }

    @Override
    public void load(InputStream stream) throws Exception {
        this.rawText=new String(stream.readAllBytes());
        this.element= JsonParser.parseString(this.getRawText());
    }

    public String getRawText() {
        return rawText;
    }
}
