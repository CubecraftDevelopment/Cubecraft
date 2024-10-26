package net.cubecraft.client.internal.handler;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.math.hitting.HitResult;
import me.gb2022.commons.math.hitting.Hittable;
import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.MouseButton;
import me.gb2022.quantum3d.device.event.KeyboardPressEvent;
import me.gb2022.quantum3d.device.event.MouseClickEvent;
import me.gb2022.quantum3d.device.event.MousePosEvent;
import me.gb2022.quantum3d.device.event.MouseScrollEvent;
import net.cubecraft.EnvironmentPath;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.chunk.ChunkCodec;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.entity.EntityLiving;
import net.cubecraft.world.entity.controller.EntityController;
import net.cubecraft.world.item.container.Inventory;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class PlayerController extends EntityController<EntityPlayer> {
    private final CubecraftClient client;
    private EntityLiving entity;
    private int slot;

    public PlayerController(CubecraftClient client, EntityLiving e) {
        this.client = client;
        this.entity = e;
        this.client.getDeviceEventBus().registerEventListener(this);
    }

    public void setEntity(EntityPlayer entity) {
        this.entity = entity;

    }

    public void tick() {
        Keyboard keyboard = this.client.getClientDeviceContext().getKeyboard();

        this.handle((EntityPlayer) entity);
        float speed;
        {
            if (keyboard.isKeyDown(KeyboardButton.KEY_W)) {
                this.moveForward();
            }
            if (keyboard.isKeyDown(KeyboardButton.KEY_S)) {
                this.moveBackward();
            }
            if (keyboard.isKeyDown(KeyboardButton.KEY_A)) {
                this.moveLeft();
            }
            if (keyboard.isKeyDown(KeyboardButton.KEY_D)) {
                this.moveRight();
            }
            if (keyboard.isKeyDown(KeyboardButton.KEY_SPACE)) {
                this.jump();
            }


            if (keyboard.isKeyDown(KeyboardButton.KEY_SPACE)) {
                if (this.entity.isFlying()) {
                    this.flyUp();
                } else {
                    this.jump();
                }
            }
            if (keyboard.isKeyDown(KeyboardButton.KEY_LEFT_SHIFT)) {
                if (entity.isFlying()) {
                    entity.yd = -0.45f;
                } else {
                    this.entity.setSneaking(true);
                }
            } else {
                this.entity.setSneaking(false);
            }
        }

        if (keyboard.isKeyDoubleClicked(KeyboardButton.KEY_SPACE, 250)) {
            this.toggleFly();
        }

        super.tick();
    }


    @EventHandler
    public void onMouseMove(MousePosEvent e) {
        if (this.client.getClientDeviceContext().getMouse().isMouseGrabbed()) {
            this.entity.turn(e.getDeltaX(), -e.getDeltaY(), 0);
        }
    }

    @Deprecated
    @EventHandler
    public void onKeyEventPressed(KeyboardPressEvent e) {
        if (e.getKey() == KeyboardButton.KEY_LEFT_CONTROL) {
            this.toggleSprint();
        }


        if (e.getKey() == KeyboardButton.KEY_F5) {
            var tag = ChunkCodec.getWorldChunkData(this.entity.getWorld().getChunk(ChunkPos.fromWorldPos(this.entity.x, this.entity.z)));

            try {
                NBT.writeZipped(tag, new FileOutputStream("E:/chunk.dat"));
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (e.getKey() == KeyboardButton.KEY_F6) {
            try {
                var tag = NBT.readZipped(new FileInputStream("E:/chunk.dat"));

                ChunkCodec.setWorldChunkData(this.entity.getWorld().getChunk(ChunkPos.fromWorldPos(this.entity.x, this.entity.z)),
                                             (NBTTagCompound) tag
                );
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }


        if (e.getKey() == KeyboardButton.KEY_O) {
            WorldChunk chunk = this.entity.getWorld().getChunk(0, 0);
            NBTTagCompound tag = ChunkCodec.getWorldChunkData(chunk);

            OutputStream stream;
            try {
                stream = new FileOutputStream(EnvironmentPath.CACHE_FOLDER + "/chunk_cap.nbt");
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            GZIPOutputStream zipOutput;
            try {
                zipOutput = new GZIPOutputStream(stream);
                NBT.write(tag, new DataOutputStream(zipOutput));
                zipOutput.close();
                stream.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (e.getKey() == KeyboardButton.KEY_P) {
            NBTTagCompound tag;

            InputStream stream;
            try {
                stream = new FileInputStream(EnvironmentPath.CACHE_FOLDER + "/chunk_cap.nbt");
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            GZIPInputStream zipOutput;
            try {
                zipOutput = new GZIPInputStream(stream);
                tag = (NBTTagCompound) NBT.read(new DataInputStream(zipOutput));
                zipOutput.close();
                stream.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ChunkCodec.setWorldChunkData(this.entity.getWorld().getChunk(0, 0), tag);
        }
    }

    @EventHandler
    public void onScroll(MouseScrollEvent e) {
        int i = (int) -e.getYOffset();
        if (i > 0) {
            i = 1;
        }
        if (i < 0) {
            i = -1;
        }
        this.slot += i;
        if (this.slot > 8) {
            this.slot = 0;
        }
        if (this.slot < 0) {
            this.slot = 8;
        }
        this.entity.getInventory().setActiveSlot(this.slot);
    }

    @EventHandler
    public void onClicked(MouseClickEvent e) {
        if (!this.isHandlingEntity()) {
            return;
        }

        if (e.getButton() == MouseButton.MOUSE_BUTTON_LEFT) {
            this.entity.attack();
        }
        if (e.getButton() == MouseButton.MOUSE_BUTTON_RIGHT) {
            this.entity.interact();
        }
        if (e.getButton() == MouseButton.MOUSE_BUTTON_MIDDLE) {
            HitResult hitResult = this.entity.hitResult;
            if (hitResult != null) {
                Hittable obj = this.entity.hitResult.getObject(Hittable.class);
                Inventory inv = this.entity.getInventory();
                inv.selectItem(obj, this.slot);
            }
        }
    }


    //todo:按键绑定


}
