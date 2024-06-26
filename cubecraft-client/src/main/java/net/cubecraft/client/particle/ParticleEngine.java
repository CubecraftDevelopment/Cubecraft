package net.cubecraft.client.particle;

import me.gb2022.commons.event.EventHandler;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.internal.entity.BlockBrakeParticle;
import net.cubecraft.client.render.model.block.BlockModel;
import net.cubecraft.event.BlockIDChangedEvent;
import net.cubecraft.internal.block.BlockType;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.entity.EntityParticle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ParticleEngine {
    protected final IWorld world;
    private final List<EntityParticle> particles = new ArrayList<>();

    public ParticleEngine(IWorld world) {
        this.world = world;
    }

    public void add(EntityParticle p) {
        this.particles.add(p);
        p.setWorld(this.world);
    }

    public void tick() {
        Iterator<EntityParticle> iterator =this.particles.iterator();
        while (iterator.hasNext()) {
            EntityParticle particle = iterator.next();
            particle.tick();
            if (particle.getLife() <= 0) {
                iterator.remove();
            }
        }
    }

    public List<EntityParticle> getParticles() {
        return particles;
    }

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
        if(Objects.equals(id, BlockType.AIR)){
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
                    this.add(new BlockBrakeParticle(e.world(), xp, yp, zp, xp - (double) x - 0.5F, yp - (double) y - 0.5F, zp - (double) z - 0.5F, tex));
                }
            }
        }
    }
}
