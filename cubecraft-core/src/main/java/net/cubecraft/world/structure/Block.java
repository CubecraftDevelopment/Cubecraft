package net.cubecraft.world.structure;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.joml.Vector3i;

import java.lang.reflect.Type;

public record Block(Vector3i from, Vector3i dest, String id, int face, String metadata) {
    public static class Deserializer implements JsonDeserializer<Block> {
        @Override
        public Block deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return null;
            //todo add deserializer
        }
    }
}
