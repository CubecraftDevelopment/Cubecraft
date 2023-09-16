package ink.flybird.cubecraft.client.internal.handler;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.render.model.block.BlockModel;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.fcommon.event.EventHandler;

import ink.flybird.cubecraft.client.internal.entity.BlockBrakeParticle;
import ink.flybird.cubecraft.world.event.BlockIDChangedEvent;

public class ParticleHandler {
    @EventHandler
    public void onBlockDestroy(BlockIDChangedEvent e) {
        int SD = 4;

        long x = e.x();
        long y = e.y();
        long z = e.z();

        String id = e.old();
        BlockModel m = ClientRenderContext.BLOCK_MODEL.get(ResourceLocation.blockModel(id + ".json").format());
        if (m == null) {
            return;
        }
        String tex = m.getParticleTexture();
        if (tex == null) {
            return;
        }

        for (int xx = 0; xx < SD; ++xx) {
            for (int yy = 0; yy < SD; ++yy) {
                for (int zz = 0; zz < SD; ++zz) {
                    double xp = (double) x + ((double) xx + 0.5F) / (double) SD;
                    double yp = (double) y + ((double) yy + 0.5F) / (double) SD;
                    double zp = (double) z + ((double) zz + 0.5F) / (double) SD;
                    CubecraftClient.CLIENT.getParticleEngine().add(new BlockBrakeParticle(e.world(), xp, yp, zp, xp - (double) x - 0.5F, yp - (double) y - 0.5F, zp - (double) z - 0.5F, tex));
                }
            }
        }
    }

}
