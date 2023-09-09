package io.flybird.cubecraft.world.structure;

import com.google.gson.*;
import io.flybird.cubecraft.world.block.EnumFacing;
import org.joml.Vector3i;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public record JsonStructure(
        StructurePart[] parts,
        Vector3i size
) implements Structure{
    @Override
    public void generate(StructureGenerator generator, Random rand, long x, long y, long z, EnumFacing facing) {
        //todo:generate
    }

    @Override
    public Vector3i getSize() {
        return this.size;
    }

    public static class Deserializer implements JsonDeserializer<JsonStructure>{

        @Override
        public JsonStructure deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject root=json.getAsJsonObject();
            Vector3i size=new Vector3i(
                    root.get("size").getAsJsonArray().get(0).getAsInt(),
                    root.get("size").getAsJsonArray().get(1).getAsInt(),
                    root.get("size").getAsJsonArray().get(2).getAsInt()
            );

            List<StructurePart> parts=new ArrayList<>();
            for (JsonElement e: root.get("elements").getAsJsonArray()){
                parts.add(context.deserialize(e,StructurePart.class));
            }

            return new JsonStructure(parts.toArray(new StructurePart[0]),size);
        }
    }
}
