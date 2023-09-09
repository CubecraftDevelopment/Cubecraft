package ink.flybird.cubecraft.client.internal.renderer.world;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.cubecraft.client.render.renderer.IWorldRenderer;
import io.flybird.cubecraft.register.SharedContext;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.entity.Entity;
import io.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.quantum3d.Camera;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.fcommon.registry.TypeItem;

@TypeItem(WorldRendererType.ENTITY)
public class EntityRenderer extends IWorldRenderer {
    private final Logger logger=new SimpleLogger("EntityRenderer");

    public EntityRenderer(Window window, IWorld world, EntityPlayer player, Camera cam, GameSetting setting) {
        super(window, world, player, cam, setting);
    }

    @Override
    public void render(float interpolationTime) {
        int visibleCount=0;
        int allCount=0;
        for (Entity e:this.world.getAllEntities()){
            allCount++;
            visibleCount++;
            try {
                ClientRenderContext.ENTITY_RENDERER.get(e.getID()).render(e);
            }catch (Exception ignored){}
        }
    }
}
