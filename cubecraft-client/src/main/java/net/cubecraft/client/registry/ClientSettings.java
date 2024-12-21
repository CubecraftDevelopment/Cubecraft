package net.cubecraft.client.registry;

import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.control.InputCommand;
import net.cubecraft.client.control.InputSettingItem;
import net.cubecraft.util.setting.GameSetting;
import net.cubecraft.util.setting.Settings;
import net.cubecraft.util.setting.item.BoolSetting;
import net.cubecraft.util.setting.item.FloatSetting;
import net.cubecraft.util.setting.item.IntSetting;
import net.cubecraft.util.setting.item.StringSetting;

@Settings
public interface ClientSettings {
    IntSetting TICK_GC = new IntSetting("", "gc_frequency", 100);

    static int getFixedViewDistance() {
        return RenderSetting.WorldSetting.ChunkSetting.getFixedViewDistance();
    }

    static void register(GameSetting setting) {
        setting.register(ClientSettings.class);

        setting.register(UISetting.class);
        setting.register(ControlSetting.class);

        setting.register(RenderSetting.class);
        setting.register(RenderSetting.WorldSetting.class);
        setting.register(RenderSetting.WorldSetting.ChunkSetting.class);
        setting.register(RenderSetting.WorldSetting.CloudSetting.class);

        setting.save();
    }

    @Settings("control")
    interface ControlSetting {
        InputSettingItem WALK_FORWARD = new InputSettingItem("*:move", "forward", InputCommand.KEY_W);
        InputSettingItem WALK_BACKWARD = new InputSettingItem("*:move", "backward", InputCommand.KEY_S);
        InputSettingItem WALK_LEFT = new InputSettingItem("*:move", "left", InputCommand.KEY_A);
        InputSettingItem WALK_RIGHT = new InputSettingItem("*:move", "right", InputCommand.KEY_D);
        InputSettingItem JUMP = new InputSettingItem("*:move", "jump", InputCommand.KEY_SPACE);
        InputSettingItem SNEAK = new InputSettingItem("*:move", "sneak", InputCommand.KEY_LEFT_SHIFT);
        InputSettingItem SPRINT = new InputSettingItem("*:move", "sprint", InputCommand.KEY_LEFT_CONTROL);
        InputSettingItem ATTACK = new InputSettingItem("*:action", "attack", InputCommand.MOUSE_BUTTON_LEFT);
        InputSettingItem INTERACT = new InputSettingItem("*:action", "interact", InputCommand.MOUSE_BUTTON_RIGHT);
        InputSettingItem SELECT = new InputSettingItem("*:inventory", "select", InputCommand.MOUSE_BUTTON_MIDDLE);
        InputSettingItem SHIFT_OFFHAND = new InputSettingItem("*:inventory", "shift-offhand", InputCommand.KEY_F);

        InputSettingItem ACTIONBAR_1 = new InputSettingItem("*:inventory", "actionbar_1", InputCommand.KEY_1);
        InputSettingItem ACTIONBAR_2 = new InputSettingItem("*:inventory", "actionbar_2", InputCommand.KEY_2);
        InputSettingItem ACTIONBAR_3 = new InputSettingItem("*:inventory", "actionbar_3", InputCommand.KEY_3);
        InputSettingItem ACTIONBAR_4 = new InputSettingItem("*:inventory", "actionbar_4", InputCommand.KEY_4);
        InputSettingItem ACTIONBAR_5 = new InputSettingItem("*:inventory", "actionbar_5", InputCommand.KEY_5);
        InputSettingItem ACTIONBAR_6 = new InputSettingItem("*:inventory", "actionbar_6", InputCommand.KEY_6);
        InputSettingItem ACTIONBAR_7 = new InputSettingItem("*:inventory", "actionbar_7", InputCommand.KEY_7);
        InputSettingItem ACTIONBAR_8 = new InputSettingItem("*:inventory", "actionbar_8", InputCommand.KEY_8);
        InputSettingItem ACTIONBAR_9 = new InputSettingItem("*:inventory", "actionbar_9", InputCommand.KEY_9);
    }

    @Settings("zoom")
    interface CameraPlugin {
        InputSettingItem SWITCH_VIEW = new InputSettingItem("*:camera", "switch-view", InputCommand.KEY_F5);
        InputSettingItem CAMERA_ZOOM = new InputSettingItem("*:camera", "zoom", InputCommand.KEY_C);
        IntSetting CAMERA_ZOOM_VALUE = new IntSetting("*:camera", "zoom-scale", -50);
        IntSetting CAMERA_ZOOM_TRANSITION = new IntSetting("*:camera", "zoom-transition", 150);
    }

    @Settings("render")
    interface RenderSetting {
        IntSetting MAX_FPS = new IntSetting("max-fps", 240);
        IntSetting INACTIVE_FPS = new IntSetting("inactive-fps", 30);
        IntSetting FXAA = new IntSetting("fxaa-quality", 0);
        BoolSetting VSYNC = new BoolSetting("vsync", true);
        BoolSetting FULL_SCREEN = new BoolSetting("fullscreen", false);

        @Settings("render:world")
        interface WorldSetting {
            IntSetting CAMERA_MODE = new IntSetting("camera-mode", 0);
            FloatSetting FOV = new FloatSetting("fov", 70.0f);

            @Settings("render:world:clouds")
            interface CloudSetting {
                BoolSetting ENABLE = new BoolSetting("enabled", true);
                IntSetting QUALITY = new IntSetting("quality", 2);
            }

            @Settings("render:world:chunk")
            interface ChunkSetting {
                IntSetting VIEW_DISTANCE = new IntSetting("view-distance", 24);
                IntSetting SHADOW_DISTANCE = new IntSetting("shadow-distance", 24);

                //quality
                BoolSetting CLASSIC_LIGHTING = new BoolSetting("quality:classic-lighting", true);
                BoolSetting AMBIENT_OCCLUSION = new BoolSetting("quality:ambient-occlusion", true);

                //performance
                BoolSetting FACE_CULLING = new BoolSetting("perf:face-fulling", true);
                BoolSetting VBO = new BoolSetting("perf:vbo", true);
                FloatSetting CACHE_SIZE_MULTIPLIER = new FloatSetting("perf:cache-size-multiplier", 1.5);

                //update
                IntSetting UPDATE_THREAD = new IntSetting("update:threads", 1);
                IntSetting MAX_UPLOAD = new IntSetting("update:max-upload", 16);
                IntSetting MAX_RECEIVE = new IntSetting("update:max-receive", 512);
                BoolSetting FORCE_REBUILD_NEAR = new BoolSetting("update:force-rebuild-near", false);

                static int getFixedViewDistance() {
                    return VIEW_DISTANCE.getValue() + 2;
                }
            }
        }

    }

    @Settings("render:ui")
    interface UISetting {
        IntSetting RENDER_INTERVAL = new IntSetting("render-interval", 16);
        BoolSetting SKIP_STUDIO_LOGO = new BoolSetting("skip-studio-logo", true);
        FloatSetting GUI_SCALE = new FloatSetting("scale", 2.0);
        StringSetting LANGUAGE = new StringSetting("language", "auto");

        static float getGUIScaleMod() {
            var width = CubecraftClient.getInstance().getWindow().getWidth();
            var height = CubecraftClient.getInstance().getWindow().getHeight();


            if (width > 2600 && height > 1500) {
                //return 1.5f;
            }

            if (width < 500 || height < 300) {
                return 0.5f;
            }

            if (width < 1100 || height < 600) {
                return 0.75f;
            }

            return 1.0f;
        }

        static float getFixedGUIScale() {
            return (float) (GUI_SCALE.getValue() * getGUIScaleMod());
        }
    }
}
