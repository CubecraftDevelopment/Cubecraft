package ink.flybird.cubecraft.client.internal.handler;

import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.EnvironmentPath;
import ink.flybird.cubecraft.world.chunk.ChunkCodec;
import ink.flybird.cubecraft.world.chunk.WorldChunk;
import ink.flybird.cubecraft.world.entity.EntityLiving;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.file.NBTBuilder;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import ink.flybird.quantum3d.device.KeyboardButton;
import ink.flybird.quantum3d.device.event.KeyboardPressEvent;
import ink.flybird.quantum3d.device.event.MousePosEvent;
import ink.flybird.quantum3d.device.event.MouseScrollEvent;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class PlayerController {
    @Deprecated
    public static final String[] list = new String[]{
            "cubecraft:stone",
            "cubecraft:stone",
            "cubecraft:stone",
            "cubecraft:stone",
            "cubecraft:stone",
            "cubecraft:stone",
            "cubecraft:stone",
            "cubecraft:stone",
            "cubecraft:blue_stained_glass"
    };

    private final CubecraftClient client;
    private EntityLiving entity;

    public PlayerController(CubecraftClient client, EntityLiving e) {
        this.client = client;
        this.entity = e;
        this.client.getDeviceEventBus().registerEventListener(this);
    }

    public void setEntity(EntityLiving entity) {
        this.entity = entity;
    }

    public void tick() {
        float xa = 0.0f;
        float ya = 0.0f;
        float speed;
        {
            if (this.client.getKeyboard().isKeyDown(KeyboardButton.KEY_W)) {
                ya -= 1;
            }
            if (this.client.getKeyboard().isKeyDown(KeyboardButton.KEY_S)) {
                ya += 1;
            }
            if (this.client.getKeyboard().isKeyDown(KeyboardButton.KEY_A)) {
                xa -= 1;
            }
            if (this.client.getKeyboard().isKeyDown(KeyboardButton.KEY_D)) {
                xa += 1;
            }
            if (entity.isFlying()) {
                if (entity.runningMode) {
                    speed = 3.5f;
                } else {
                    speed = 2f;
                }
            } else {
                if (entity.runningMode) {
                    speed = 1.38f;
                } else {
                    speed = 0.9f;
                }
            }

            if (this.client.getKeyboard().isKeyDown(KeyboardButton.KEY_SPACE)) {
                if (!this.entity.isFlying()) {
                    if (this.entity.inLiquid()) {
                        this.entity.yd += 0.13f;
                    } else if (this.entity.isOnGround()) {
                        this.entity.yd = 0.45f;
                    }
                } else {
                    entity.yd = 0.45f;
                }
            }
            if (this.client.getKeyboard().isKeyDown(KeyboardButton.KEY_LEFT_SHIFT)) {
                if (entity.isFlying()) {
                    entity.yd = -0.45f;
                } else {
                    this.entity.sneak = !this.entity.sneak;
                }
            }
            this.entity.moveRelative(xa, ya, this.entity.isOnGround() ? this.entity.inLiquid() ? 0.02f * speed : 0.1f * speed : 0.02f * speed);
        }

        if (this.client.getKeyboard().isKeyDoubleClicked(KeyboardButton.KEY_SPACE, 250)) {
            this.entity.setFlying(!this.entity.isFlying());
        }
    }

    public void setSelectedSlot(int slot) {
        this.entity.selectedBlockID = list[slot];
    }


    @EventHandler
    public void onMouseMove(MousePosEvent e) {
        if (this.client.getMouse().isMouseGrabbed()) {
            this.entity.turn(e.getDeltaX(), -e.getDeltaY(), 0);
        }
    }

    @EventHandler
    public void onKeyEventPressed(KeyboardPressEvent e) {
        if (e.getKey() == KeyboardButton.KEY_LEFT_CONTROL) {
            entity.runningMode = !entity.runningMode;
        }
        if (e.getKey() == KeyboardButton.KEY_R) {
            ClientSharedContext.CLIENT_SETTING.load();
        }
        if (e.getKey() == KeyboardButton.KEY_O) {
            WorldChunk chunk = this.entity.getWorld().getChunk(0, 0);
            NBTTagCompound tag = ChunkCodec.getWorldChunkData(chunk);

            OutputStream stream = null;
            try {
                stream = new FileOutputStream(EnvironmentPath.CACHE_FOLDER + "/chunk_cap.nbt");
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            GZIPOutputStream zipOutput = null;
            try {
                zipOutput = new GZIPOutputStream(stream);
                NBTBuilder.write(tag, new DataOutputStream(zipOutput));
                zipOutput.close();
                stream.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (e.getKey() == KeyboardButton.KEY_P) {
            NBTTagCompound tag;

            InputStream stream = null;
            try {
                stream = new FileInputStream(EnvironmentPath.CACHE_FOLDER + "/chunk_cap.nbt");
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            GZIPInputStream zipOutput = null;
            try {
                zipOutput = new GZIPInputStream(stream);
                tag = (NBTTagCompound) NBTBuilder.read(new DataInputStream(zipOutput));
                zipOutput.close();
                stream.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ChunkCodec.setWorldChunkData(this.entity.getWorld().getChunk(0, 0), tag);
        }
    }

    @EventHandler
    public void onMouseScroll(MouseScrollEvent e) {
        //todo:add scroll logic
    }


    //todo:按键绑定，内置服务端，toml设置
}
