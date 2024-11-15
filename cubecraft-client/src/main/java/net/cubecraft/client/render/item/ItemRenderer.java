package net.cubecraft.client.render.item;

import me.gb2022.quantum3d.memory.LWJGLBufferAllocator;
import me.gb2022.quantum3d.render.vertex.DrawMode;
import me.gb2022.quantum3d.render.vertex.VertexBuilderAllocator;
import me.gb2022.quantum3d.render.vertex.VertexBuilderUploader;
import me.gb2022.quantum3d.render.vertex.VertexFormat;
import net.cubecraft.client.ClientRenderContext;
import net.cubecraft.client.render.chunk.RenderBatch;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainers;
import net.cubecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public interface ItemRenderer {
    VertexBuilderAllocator ALLOCATOR = new VertexBuilderAllocator(new LWJGLBufferAllocator());
    Map<String, RenderBatch> CACHED_MODELS = new HashMap<>();

    static void renderBlockGUI(ItemStack stack, int x, int y, int size) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y + size * 1.32f, 0.0F);

        GL11.glScalef(size, size, size);
        GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-1.5F, 0.5F, -0.5F);
        GL11.glScalef(1.0F, -1.0F, 1.0F);

        GL11.glEnable(3553);

        var block = stack.getType();
        var renderer = ClientRenderContext.BLOCK_RENDERERS.get(block);

        if (renderer == null) {
            GL11.glPopMatrix();
            return;
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);

        var builder = ALLOCATOR.create(VertexFormat.V3F_C4F_T2F, DrawMode.QUADS, 128);
        builder.allocate();

        for (var layer : ChunkLayerContainers.REGISTRY.entries()) {
            builder.reset();

            for (var f = 0; f < 7; f++) {
                renderer.render(null, null, layer, f, 1, 1, 1, builder);
            }

            layer.get().getTexture().get().bind();
            VertexBuilderUploader.uploadPointer(builder);
        }

        ALLOCATOR.free(builder);
        GL11.glPopMatrix();
    }

    static RenderBatch getModel(ItemStack stack) {
        var id = stack.getType();

        if (CACHED_MODELS.containsKey(id)) {
            return CACHED_MODELS.get(id);
        }
        var batch = bake(stack);

        CACHED_MODELS.put(id, batch);

        return batch;
    }

    static RenderBatch bake(ItemStack stack) {
        var block = stack.getType();
        var renderer = ClientRenderContext.BLOCK_RENDERERS.get(block);

        if (renderer == null) {
            return null;
        }

        var batch = new RenderBatch.ListRenderBatch();
        batch.allocate();

        GL11.glNewList(batch.getHandle(), GL11.GL_COMPILE);

        GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-1.5F, 0.5F, -0.5F);
        GL11.glScalef(1.0F, -1.0F, 1.0F);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);

        var builder = ALLOCATOR.create(VertexFormat.V3F_C4F_T2F, DrawMode.QUADS, 128);
        builder.allocate();

        try {
            for (var layer : ChunkLayerContainers.REGISTRY.entries()) {
                builder.reset();

                for (var f = 0; f < 7; f++) {
                    renderer.render(null, null, layer, f, 1, 1, 1, builder);
                }

                layer.get().getTexture().get().bind();
                VertexBuilderUploader.uploadPointer(builder);
            }

        }catch (Throwable e) {
            ALLOCATOR.free(builder);
            GL11.glEndList();

            return null;
        }

        GL11.glEndList();
        ALLOCATOR.free(builder);

        return batch;
    }
}
