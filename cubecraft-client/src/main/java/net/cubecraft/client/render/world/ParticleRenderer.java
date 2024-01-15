package net.cubecraft.client.render.world;

import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.internal.renderer.world.WorldRendererType;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.entity.EntityParticle;
import net.cubecraft.world.entity.particle.ParticleEngine;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.culling.FrustumCuller;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import me.gb2022.quantum3d.device.Window;

import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.registry.TypeItem;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

//todo:全局透明
//todo:修复贴图丢失
@TypeItem(WorldRendererType.PARTICLE)
public class ParticleRenderer extends IWorldRenderer {
    private final FrustumCuller frustum;
    private ParticleEngine particleEngine;
    private int successSize;

    public ParticleRenderer() {
        this.frustum = new FrustumCuller();
    }

    public void setParticleEngine(ParticleEngine particleEngine) {
        this.particleEngine = particleEngine;
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


    public void render(RenderType type, float interpolationTime) {
        if (type != RenderType.ALPHA) {
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
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        ArrayList<EntityParticle> particles = new ArrayList<>(/*this.particleEngine.getParticles()*/);
        for (EntityParticle p : particles) {
            AABB aabb = new AABB(p.x - 0.1, p.y - 0.1, p.z - 0.1, p.x + 0.1, p.y + 0.1, p.z + 0.1);
            if (this.frustum.aabbVisible(this.camera.castAABB(aabb))) {
                i++;
                VertexBuilder builder = VertexBuilderAllocator.createByPrefer(128);
                builder.begin();
                //p.render(builder, interpolationTime, xa, ya, za, xa2, za2);
                builder.end();
                GL11.glPushMatrix();
                this.camera.setupObjectCamera(new Vector3d(p.x, p.y, p.z));
                builder.uploadPointer();
                builder.free();
                GL11.glPopMatrix();
            }
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        this.successSize=i;
    }
}