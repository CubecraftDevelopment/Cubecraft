package ink.flybird.cubecraft.client.internal.renderer.world.chunk;

import ink.flybird.cubecraft.client.CubecraftClient;

//todo:世界区块和渲染区块线程池
//todo:完善服务端
public final class RenderChunkFactory{

    public RenderChunk create(){
        boolean useVBO= CubecraftClient.CLIENT.getGameSetting().getValueAsBoolean("client.render.terrain.useVBO", false);
        return null;
    }
}
