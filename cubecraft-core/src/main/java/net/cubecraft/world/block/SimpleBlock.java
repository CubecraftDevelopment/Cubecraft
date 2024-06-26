package net.cubecraft.world.block;

import net.cubecraft.world.block.property.BlockProperty;
import me.gb2022.commons.math.AABB;
import com.google.gson.*;
import net.cubecraft.world.block.property.attribute.SimpleBooleanProperty;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

@Deprecated
public class SimpleBlock extends Block{
    public final EnumFacing[] enumFacings;
    public final AABB[] collisionBoxSizes;
    public final AABB[] selectionBoxSizes;
    public final float resistance,density,hardness;
    public final int opacity;
    public final boolean isSolid;
    private final String id;
    private final String[] tags;
    private final int light;

    public SimpleBlock(EnumFacing[] enumFacings, AABB[] collisionBoxSizes, AABB[] selectionBoxSizes, float resistance, float density, float hardness, int opacity, boolean isSolid, String id, String[] tags, int light) {
        super(id);
        this.enumFacings = enumFacings;
        this.collisionBoxSizes = collisionBoxSizes;
        this.selectionBoxSizes = selectionBoxSizes;
        this.resistance = resistance;
        this.density = density;
        this.hardness = hardness;
        this.opacity = opacity;
        this.isSolid = isSolid;
        this.id = id;
        this.tags = tags;
        this.light = light;

        this.cachedSolidValue=isSolid;
    }


    @Override
    public void initPropertyMap(Map<String, BlockProperty<?>> map) {

    }

    @Override
    public String[] getBehaviorList() {
        return new String[0];
    }

    @Override
    public EnumFacing[] getEnabledFacings() {
        return this.enumFacings;
    }

    @Override
    public AABB[] getCollisionBoxSizes() {
        return this.collisionBoxSizes;
    }

    @Override
    public AABB[] getSelectionBoxSizes() {
        return this.selectionBoxSizes;
    }

    @Override
    public float getResistance() {
        return this.resistance;
    }

    @Override
    public float getDensity() {
        return this.density;
    }

    @Override
    public float getHardNess() {
        return this.hardness;
    }

    @Override
    public int opacity() {
        return this.opacity;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public String[] getTags() {
        return this.tags;
    }

    @Override
    public int light(){
        return this.light;
    }

    public static class JDeserializer implements JsonDeserializer<SimpleBlock> {
        @Override
        public SimpleBlock deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {


            JsonObject obj = jsonElement.getAsJsonObject();

            ArrayList<EnumFacing> facing = new ArrayList<>();
            for (JsonElement i : obj.get("facings").getAsJsonArray()) {
                if (!facing.contains(EnumFacing.fromId(i.getAsInt()))) {
                    facing.add(EnumFacing.fromId(i.getAsInt()));
                }
            }

            ArrayList<AABB> collisions = new ArrayList<>();
            for (JsonElement i : obj.get("collisions").getAsJsonArray()) {
                JsonArray arr = i.getAsJsonArray();
                collisions.add(new AABB(
                        arr.get(0).getAsDouble(),
                        arr.get(1).getAsDouble(),
                        arr.get(2).getAsDouble(),
                        arr.get(3).getAsDouble(),
                        arr.get(4).getAsDouble(),
                        arr.get(5).getAsDouble()
                ));
            }

            ArrayList<AABB> selections = new ArrayList<>();
            for (JsonElement i : obj.get("collisions").getAsJsonArray()) {
                JsonArray arr = i.getAsJsonArray();
                collisions.add(new AABB(
                        arr.get(0).getAsDouble(),
                        arr.get(1).getAsDouble(),
                        arr.get(2).getAsDouble(),
                        arr.get(3).getAsDouble(),
                        arr.get(4).getAsDouble(),
                        arr.get(5).getAsDouble()
                ));
            }

            ArrayList<String> tag = new ArrayList<>();
            for (JsonElement i : obj.get("tags").getAsJsonArray()) {
                if (!facing.contains(EnumFacing.fromId(i.getAsInt()))) {
                    facing.add(EnumFacing.fromId(i.getAsInt()));
                }
            }

            return new SimpleBlock(
                    facing.toArray(new EnumFacing[0]),
                    collisions.toArray(new AABB[0]),
                    selections.toArray(new AABB[0]),
                    obj.get("resistance").getAsInt(),
                    obj.get("density").getAsInt(),
                    obj.get("hardness").getAsInt(),
                    obj.get("opacity").getAsInt(),
                    obj.get("solid").getAsBoolean(),
                    obj.get("id").getAsString(),
                    tag.toArray(new String[0]),
                    obj.get("light").getAsInt()
            );
        }
    }
}
