package net.cubecraft.client.render.model.block;

import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
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

    public abstract void initializeModel(Set<TextureAsset> textureList);
}