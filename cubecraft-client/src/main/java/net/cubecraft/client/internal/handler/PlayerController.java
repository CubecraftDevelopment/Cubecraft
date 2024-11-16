package net.cubecraft.client.internal.handler;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.math.hitting.HitResult;
import me.gb2022.commons.math.hitting.Hittable;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.event.AnyClickInputEvent;
import me.gb2022.quantum3d.device.event.MousePosEvent;
import me.gb2022.quantum3d.device.event.MouseScrollEvent;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.control.InputCommand;
import net.cubecraft.client.control.InputSettingItem;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.entity.EntityLiving;
import net.cubecraft.world.entity.controller.EntityController;
import net.cubecraft.world.item.container.Inventory;

public final class PlayerController extends EntityController<EntityPlayer> {
    public static final InputSettingItem WALK_FORWARD = new InputSettingItem("move", "forward", InputCommand.KEYBOARD_W);
    public static final InputSettingItem WALK_BACKWARD = new InputSettingItem("move", "forward", InputCommand.KEYBOARD_S);
    public static final InputSettingItem WALK_LEFT = new InputSettingItem("move", "forward", InputCommand.KEYBOARD_A);
    public static final InputSettingItem WALK_RIGHT = new InputSettingItem("move", "forward", InputCommand.KEYBOARD_D);

    public static final InputSettingItem JUMP = new InputSettingItem("move", "jump", InputCommand.KEYBOARD_SPACE);
    public static final InputSettingItem SNEAK = new InputSettingItem("move", "sneak", InputCommand.KEYBOARD_LEFT_SHIFT);
    public static final InputSettingItem SPRINT = new InputSettingItem("move", "sprint", InputCommand.KEYBOARD_LEFT_CONTROL);

    public static final InputSettingItem ATTACK = new InputSettingItem("action", "attack", InputCommand.MOUSE_BUTTON_LEFT);
    public static final InputSettingItem INTERACT = new InputSettingItem("action", "interact", InputCommand.MOUSE_BUTTON_RIGHT);

    public static final InputSettingItem SELECT = new InputSettingItem("inventory", "select", InputCommand.MOUSE_BUTTON_MIDDLE);
    public static final InputSettingItem SHIFT_OFFHAND = new InputSettingItem("inventory", "select", InputCommand.KEYBOARD_F);

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

        if (JUMP.isActive(keyboard, mouse)) {
            if (this.entity.isFlying()) {
                this.flyUp();
            } else {
                this.jump();
            }
        }
        if (SNEAK.isActive(keyboard, mouse)) {
            if (entity.isFlying()) {
                entity.yd = -0.35f;
            } else {
                this.entity.setSneaking(true);
            }
        } else {
            this.entity.setSneaking(false);
        }

        if (WALK_FORWARD.isActive(keyboard, mouse)) {
            this.moveForward();
        }
        if (WALK_BACKWARD.isActive(keyboard, mouse)) {
            this.moveBackward();
        }
        if (WALK_LEFT.isActive(keyboard, mouse)) {
            this.moveLeft();
        }
        if (WALK_RIGHT.isActive(keyboard, mouse)) {
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
        if (SPRINT.isTriggered(event)) {
            this.toggleSprint();
        }

        if (ATTACK.isTriggered(event)) {
            this.entity.attack();
        }
        if (INTERACT.isTriggered(event)) {
            this.entity.interact();
        }
        if (SELECT.isTriggered(event)) {
            HitResult hitResult = this.entity.hitResult;
            if (hitResult != null) {
                Hittable obj = this.entity.hitResult.getObject(Hittable.class);
                Inventory inv = this.entity.getInventory();
                inv.selectItem(obj, this.slot);
            }
        }
    }
}
