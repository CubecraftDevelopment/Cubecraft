package ink.flybird.quantum3d.platform;

import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.quantum3d.event.*;
import ink.flybird.fcommon.event.EventBus;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallback;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class JoystickController {
    private static final EventBus eventBus = new SimpleEventBus();
    private static final JoystickCallback callback = new JoystickCallback();

    public static void install() {
        GLFW.glfwSetJoystickCallback(callback);
    }

    public static void uninstall() {
        callback.free();
    }

    public static EventBus getEventBus() {
        return eventBus;
    }

    public static class JoystickCallback extends GLFWJoystickCallback {
        @Override
        public void invoke(int jid, int event) {
            if (event == GLFW.GLFW_CONNECTED) {
                getEventBus().callEvent(new JoystickConnectedEvent(jid));
            }
            if (event == GLFW.GLFW_DISCONNECTED) {
                getEventBus().callEvent(new JoystickDisconnectedEvent(jid));
            }

            FloatBuffer buffer = GLFW.glfwGetJoystickAxes(jid);
            if (buffer != null) {
                getEventBus().callEvent(new JoyStickAxisEvent(buffer.array()));
            }

            ByteBuffer btn = GLFW.glfwGetJoystickButtons(jid);
            if (btn != null) {
                getEventBus().callEvent(new JoyStickButtonEvent(btn.array()));
            }

            ByteBuffer hats = GLFW.glfwGetJoystickHats(jid);
            if (hats != null) {
                getEventBus().callEvent(new JoyStickHatsEvent(hats.array()));
            }


        }
    }
}
