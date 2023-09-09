package ink.flybird.cubecraft.client.internal.handler;

import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.quantum3d.device.KeyboardButton;
import ink.flybird.quantum3d.device.event.KeyboardPressEvent;
import ink.flybird.quantum3d.device.event.MousePosEvent;
import ink.flybird.quantum3d.device.event.MouseScrollEvent;
import io.flybird.cubecraft.util.GameSetting;
import io.flybird.cubecraft.world.entity.EntityLiving;


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
            if (entity.flying) {
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
                if (!this.entity.flying) {
                    if (this.entity.inLiquid()) {
                        this.entity.yd += 0.13f;
                    } else if (this.entity.onGround) {
                        this.entity.yd = 0.45f;
                    }
                } else {
                    entity.yd = 0.45f;
                }
            }
            if (this.client.getKeyboard().isKeyDown(KeyboardButton.KEY_LEFT_SHIFT)) {
                if (entity.flying) {
                    entity.yd = -0.45f;
                } else {
                    this.entity.sneak = !this.entity.sneak;
                }
            }
            this.entity.moveRelative(xa, ya, this.entity.onGround ? this.entity.inLiquid() ? 0.02f * speed : 0.1f * speed : 0.02f * speed);
        }

        if (this.client.getKeyboard().isKeyDoubleClicked(KeyboardButton.KEY_SPACE, 250)) {
            this.entity.flying = !this.entity.flying;
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
    }

    @EventHandler
    public void onMouseScroll(MouseScrollEvent e) {
        //todo:add scroll logic
    }



    //todo:按键绑定，内置服务端，toml设置
}
