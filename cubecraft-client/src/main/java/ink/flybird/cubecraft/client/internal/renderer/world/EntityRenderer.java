package ink.flybird.cubecraft.client.internal.renderer.world;

import ink.flybird.cubecraft.client.render.RenderType;
import ink.flybird.cubecraft.client.render.renderer.IWorldRenderer;
import ink.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.entity.Entity;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.draw.DrawMode;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import org.lwjgl.opengl.GL11;

//todo: 实体加载，blockmap
@TypeItem(WorldRendererType.ENTITY)
public class EntityRenderer extends IWorldRenderer {
    private final Logger logger = new SimpleLogger("EntityRenderer");

    public EntityRenderer(Window window, IWorld world, EntityPlayer player, Camera cam, GameSetting setting) {
        super(window, world, player, cam, setting);
    }


    @Override
    public void preRender() {
        super.preRender();
    }

    @Override
    public void preRender(RenderType type, float delta) {
        super.preRender(type, delta);
    }

    @Override
    public void render(RenderType type, float delta) {
        super.render(type, delta);
    }

    @Override
    public void postRender(RenderType type, float delta) {
        super.postRender(type, delta);
    }

    @Override
    public void postRender() {
        super.postRender();
    }
}
