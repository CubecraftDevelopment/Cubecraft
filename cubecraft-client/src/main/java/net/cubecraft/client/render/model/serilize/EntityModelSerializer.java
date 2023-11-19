package net.cubecraft.client.render.model.serilize;

import net.cubecraft.client.render.model.object.EntityModel;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class EntityModelSerializer implements JsonDeserializer<EntityModel> {
    @Override
    public EntityModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }
}
