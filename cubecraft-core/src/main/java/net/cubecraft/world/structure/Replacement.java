package net.cubecraft.world.structure;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.cubecraft.world.block.BlockState;
import net.cubecraft.world.block.EnumFacing;
import org.joml.Vector3i;

import java.lang.reflect.Type;
import java.util.Random;

public record Replacement(String id, byte face, byte meta, Vector3i[] locations ) implements StructurePart{
    @Override
    public void generate(Structure s,StructureGenerator context,Random rand, long x, long y, long z, EnumFacing facing) {
        for (Vector3i loc:locations()){
            context.setBlock(x+loc.x,y+loc.y,z+loc.z,new BlockState(id,face,meta));
        }
    }

    public static class Deserializer implements JsonDeserializer<Replacement>{
        @Override
        public Replacement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return null;
            //todo add deserializer
        }
    }
}
