package net.cubecraft.client.internal.plugins;

import me.gb2022.commons.event.EventHandler;
import net.cubecraft.client.ClientComponent;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.internal.entity.BlockBrakeParticle;
import net.cubecraft.client.render.model.block.BlockModel;
import net.cubecraft.event.world.BlockBreakEvent;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.world.WorldContext;
import net.cubecraft.world.block.blocks.Blocks;

public final class ParticlePlugin extends ClientComponent {
    public static final int BLOCK_BREAK_COUNT = 4;

    @Override
    public void worldContextChange(WorldContext context) {
        if (context == null || context.getWorld() == null) {
            return;
        }
        context.getWorld().getEventBus().registerEventListener(this);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var x = event.getX();
        var y = event.getY();
        var z = event.getZ();

        var id = Blocks.REGISTRY.name(event.getOriginalBlock());
        var engine = CubecraftClient.getInstance().getParticleEngine();
        var model = BlockModel.REGISTRY.get(ResourceLocation.blockModel(id + ".json").format());

        if (model == null) {
            return;
        }
        var tex = model.getParticleTexture();
        if (tex == null) {
            return;
        }

        for (int xx = 0; xx < BLOCK_BREAK_COUNT; ++xx) {
            for (int yy = 0; yy < BLOCK_BREAK_COUNT; ++yy) {
                for (int zz = 0; zz < BLOCK_BREAK_COUNT; ++zz) {
                    var xp = x + (xx + 0.5F) / (double) BLOCK_BREAK_COUNT;
                    var yp = y + (yy + 0.5F) / (double) BLOCK_BREAK_COUNT;
                    var zp = z + (zz + 0.5F) / (double) BLOCK_BREAK_COUNT;

                    var xm = xp - x - 0.5F;
                    var ym = yp - y - 0.5F;
                    var zm = zp - z - 0.5F;

                    engine.add(new BlockBrakeParticle(event.getWorld(), xp, yp, zp, xm, ym, zm, tex));
                }
            }
        }
    }
}
