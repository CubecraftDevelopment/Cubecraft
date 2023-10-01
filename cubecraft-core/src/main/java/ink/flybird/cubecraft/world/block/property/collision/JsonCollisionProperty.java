package ink.flybird.cubecraft.world.block.property.collision;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ink.flybird.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.fcommon.math.AABB;

public class JsonCollisionProperty extends CollisionProperty {
    private final AABB[] aabbs;

    public JsonCollisionProperty(JsonElement element) {
        JsonArray array = element.getAsJsonArray();
        this.aabbs = new AABB[array.size()];
        int i = 0;
        for (JsonElement aabbElement : array) {
            JsonArray bound = aabbElement.getAsJsonArray();
            this.aabbs[i] = new AABB(
                    bound.get(0).getAsDouble(),
                    bound.get(1).getAsDouble(),
                    bound.get(2).getAsDouble(),
                    bound.get(3).getAsDouble(),
                    bound.get(4).getAsDouble(),
                    bound.get(5).getAsDouble());
            i++;
        }
    }

    @Override
    public AABB[] get(IBlockAccess access) {
        return this.aabbs;
    }
}
