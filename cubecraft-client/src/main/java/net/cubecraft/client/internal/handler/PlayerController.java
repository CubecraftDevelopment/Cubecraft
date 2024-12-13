package net.cubecraft.client.internal.handler;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.math.hitting.HitResult;
import me.gb2022.commons.math.hitting.Hittable;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.event.AnyClickInputEvent;
import me.gb2022.quantum3d.device.event.MousePosEvent;
import me.gb2022.quantum3d.device.event.MouseScrollEvent;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.registry.ClientSettings.ControlSetting;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.entity.EntityLiving;
import net.cubecraft.world.entity.controller.EntityController;
import net.cubecraft.world.item.container.Inventory;

public final class PlayerController extends EntityController<EntityPlayer> {
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
        this.handle(entity);
    }

    public void tick() {
        this.handle((EntityPlayer) entity);

        var keyboard = this.client.getClientDeviceContext().getKeyboard();
        var mouse = this.client.getClientDeviceContext().getMouse();

        if (ControlSetting.JUMP.isActive(keyboard, mouse)) {
            if (this.entity.isFlying()) {
                this.flyUp();
            } else {
                this.jump();
            }
        }
        if (ControlSetting.SNEAK.isActive(keyboard, mouse)) {
            if (entity.isFlying()) {
                entity.yd = -0.35f;
            } else {
                this.entity.setSneaking(true);
            }
        } else {
            this.entity.setSneaking(false);
        }

        if (ControlSetting.WALK_FORWARD.isActive(keyboard, mouse)) {
            this.moveForward();
        }
        if (ControlSetting.WALK_BACKWARD.isActive(keyboard, mouse)) {
            this.moveBackward();
        }
        if (ControlSetting.WALK_LEFT.isActive(keyboard, mouse)) {
            this.moveLeft();
        }
        if (ControlSetting.WALK_RIGHT.isActive(keyboard, mouse)) {
            this.moveRight();
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

    //@EventHandler
    public void onInput(AnyClickInputEvent event) {
        if (ControlSetting.SPRINT.isTriggered(event)) {
            this.toggleSprint();
        }

        if (ControlSetting.ATTACK.isTriggered(event)) {
            this.entity.attack();
        }
        if (ControlSetting.INTERACT.isTriggered(event)) {
            this.entity.interact();
        }
        if (ControlSetting.SELECT.isTriggered(event)) {
            HitResult hitResult = this.entity.hitResult;
            if (hitResult != null) {
                Hittable obj = this.entity.hitResult.getObject(Hittable.class);
                Inventory inv = this.entity.getInventory();
                inv.selectItem(obj, this.slot);
            }
        }
    }
}
