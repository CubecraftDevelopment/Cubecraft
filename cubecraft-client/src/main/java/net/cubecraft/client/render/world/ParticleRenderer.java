package net.cubecraft.client.render.world;

import me.gb2022.quantum3d.legacy.draw.LegacyVertexBuilder;
import me.gb2022.quantum3d.legacy.draw.VertexBuilderAllocator;
import me.gb2022.quantum3d.util.FrustumCuller;
import me.gb2022.commons.math.AABB;
import me.gb2022.commons.registry.RegisterMap;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.internal.renderer.world.WorldRendererType;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.client.render.renderer.IParticleRenderer;
import net.cubecraft.world.entity.EntityParticle;
import net.cubecraft.client.particle.ParticleEngine;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

//todo:全局透明
//todo:修复贴图丢失
@TypeItem(WorldRendererType.PARTICLE)
public class ParticleRenderer extends IWorldRenderer {
    @SuppressWarnings("rawtypes")
    public static final RegisterMap<IParticleRenderer> PARTICLE_RENDERERS = new RegisterMap<>(IParticleRenderer.class);

    private final FrustumCuller frustum;
    private final ParticleEngine particleEngine;
    private int successSize;

    public ParticleRenderer() {
        this.frustum = new FrustumCuller();
        this.particleEngine = ClientSharedContext.getClient().getParticleEngine();
    }

    public void init() {
        this.world.getEventBus().registerEventListener(this);
        ClientSharedContext.QUERY_HANDLER.registerCallback(this.getID(), (arg -> switch (arg) {
            case "success_size" -> this.successSize;
            // case "all_size" -> this.particleEngine.getParticles().size();
            default -> 0;
        }));
    }


    @Override
    public void preRender() {
        this.camera.setUpGlobalCamera();
        this.frustum.calculateFrustum();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void render(RenderType type, float delta) {
        if (type != RenderType.ALPHA) {
            return;
        }
        if(this.particleEngine==null){
            return;
        }

        this.camera.setUpGlobalCamera();
        double yRot = this.camera.getRotation().y;
        double xRot = this.camera.getRotation().x;

        float xa = -((float) Math.cos(yRot * Math.PI / 180.0));
        float za = -((float) Math.sin(yRot * Math.PI / 180.0));
        float xa2 = -za * (float) Math.sin(xRot * Math.PI / 180.0);
        float za2 = xa * (float) Math.sin(xRot * Math.PI / 180.0);
        float ya = (float) Math.cos(xRot * Math.PI / 180.0);

        int i = 0;
        GL11.glColor4f(0.8f, 0.8f, 0.8f, 1.0f);

        ArrayList<EntityParticle> particles = new ArrayList<>(this.particleEngine.getParticles());
        for (EntityParticle p : particles) {
            String s=p.getID();
            IParticleRenderer renderer = PARTICLE_RENDERERS.get(p.getID());
            if (renderer == null) {
                continue;
            }

            AABB aabb = new AABB(p.x - 0.1, p.y - 0.1, p.z - 0.1, p.x + 0.1, p.y + 0.1, p.z + 0.1);
            if (this.frustum.aabbVisible(this.camera.castAABB(aabb))) {
                i++;
                LegacyVertexBuilder builder = VertexBuilderAllocator.createByPrefer(128);
                builder.begin();
                renderer.render(p, builder, delta, xa, ya, za, xa2, za2);
                builder.end();
                GL11.glPushMatrix();
                this.camera.setupObjectCamera(new Vector3d(p.x, p.y, p.z));
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                builder.uploadPointer();
                builder.free();
                GL11.glPopMatrix();
            }
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        this.successSize = i;
    }
}