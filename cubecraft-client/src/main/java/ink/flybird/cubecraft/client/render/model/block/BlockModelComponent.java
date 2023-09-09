package ink.flybird.cubecraft.client.render.model.block;

import ink.flybird.cubecraft.client.resources.resource.ImageResource;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.block.IBlockAccess;
import ink.flybird.quantum3d.draw.VertexBuilder;
import org.joml.Vector3d;

import java.util.Set;

public abstract class BlockModelComponent {
    protected final String layer;
    protected final Vector3d start, end;

    protected BlockModelComponent(String layer, Vector3d start, Vector3d end) {
        this.layer = layer;
        this.start = start;
        this.end = end;
    }


    //model
    public String getRenderLayer() {
        return this.layer;
    }

    public Vector3d getStart() {
        return start;
    }

    public Vector3d getEnd() {
        return end;
    }


    //abstract method
    public abstract void render(VertexBuilder builder, String layer, IWorld world, IBlockAccess blockAccess, double renderX, double renderY, double renderZ);

    public abstract void renderAsItem(VertexBuilder builder, double renderX, double renderY, double renderZ);

    public abstract void initializeModel(Set<ImageResource> textureList);
}