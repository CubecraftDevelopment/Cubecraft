package net.cubecraft.client.internal.plugins;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.quantum3d.device.event.KeyboardPressEvent;
import me.gb2022.quantum3d.device.event.KeyboardReleaseEvent;
import me.gb2022.quantum3d.util.camera.PerspectiveCamera;
import net.cubecraft.client.ClientComponent;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.registry.ClientSettings;
import net.cubecraft.client.render.LevelRenderer;
import net.cubecraft.client.util.AnimatedValue;

public final class CameraPlugin extends ClientComponent {
    private final AnimatedValue zoom = new AnimatedValue();
    private LevelRenderer renderer;

    @Override
    public void clientSetup(CubecraftClient client) {
        this.renderer = client.getComponent(LevelRenderer.class).orElseThrow();

        client.getClientEventBus().registerEventListener(this);
        client.getDeviceEventBus().registerEventListener(this);
    }

    @EventHandler
    public void checkFov(KeyboardReleaseEvent event) {
        if (ClientSettings.CameraPlugin.CAMERA_ZOOM.isAnyTriggered(event.getKey(), null)) {
            var transition = ClientSettings.CameraPlugin.CAMERA_ZOOM_TRANSITION.getValue();

            this.zoom.animateCubicTo(0, transition);
        }
    }

    @EventHandler
    public void checkFov(KeyboardPressEvent event) {
        if (ClientSettings.CameraPlugin.CAMERA_ZOOM.isAnyTriggered(event.getKey(), null)) {
            var transition = ClientSettings.CameraPlugin.CAMERA_ZOOM_TRANSITION.getValue();
            var value = ClientSettings.CameraPlugin.CAMERA_ZOOM_VALUE.getValue();

            this.zoom.animateCubicTo(value, transition);
        }
    }

    @Override
    public void render(DisplayScreenInfo info, float delta) {
        if (this.renderer == null) {
            return;
        }

        this.zoom.update();

        if (!(this.renderer.getViewCamera() instanceof PerspectiveCamera cc)) {
            return;
        }

        var fov = ClientSettings.RenderSetting.WorldSetting.FOV.getValue();

        var offset = this.zoom.getValue();

        if (Double.isNaN(offset)) {
            return;
        }

        cc.setFov((float) (fov + offset));
    }
}
