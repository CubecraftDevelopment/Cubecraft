package net.cubecraft.client.registry;

import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.control.InputCommand;
import net.cubecraft.client.control.InputSettingItem;
import net.cubecraft.util.setting.ModernGameSetting;
import net.cubecraft.util.setting.Settings;
import net.cubecraft.util.setting.item.BoolSetting;
import net.cubecraft.util.setting.item.FloatSetting;
import net.cubecraft.util.setting.item.IntSetting;
import net.cubecraft.util.setting.item.StringSetting;

@Settings
public interface ClientSettings {
    IntSetting TICK_GC = new IntSetting( "gc_frequency", 100);

    static int getFixedViewDistance() {
        return RenderSetting.WorldSetting.ChunkSetting.getFixedViewDistance();
    }

    static void register(ModernGameSetting setting) {
        setting.register(ClientSettings.class);

        setting.register(UISetting.class);
        setting.register(ControlSetting.class);

        setting.register(RenderSetting.class);
        setting.register(RenderSetting.WorldSetting.class);
        setting.register(RenderSetting.WorldSetting.ChunkSetting.class);


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
            @Settings("render:clouds")
            interface CloudsSetting {

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
            var width = ClientSharedContext.getClient().getWindow().getWidth();
            var height = ClientSharedContext.getClient().getWindow().getHeight();


            if (width > 2600 && height > 1500) {
                return 1.5f;
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
