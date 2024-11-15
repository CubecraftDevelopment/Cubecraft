package me.gb2022.quantum3d.util;

import me.gb2022.commons.event.EventBus;
import me.gb2022.commons.event.SimpleEventBus;
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
                //getEventBus().callEvent();
            }
            if (event == GLFW.GLFW_DISCONNECTED) {
                //getEventBus().callEvent();
            }

            FloatBuffer buffer = GLFW.glfwGetJoystickAxes(jid);
            if (buffer != null) {
                // getEventBus().callEvent();
            }

            ByteBuffer btn = GLFW.glfwGetJoystickButtons(jid);
            if (btn != null) {
                //getEventBus().callEvent();
            }

            ByteBuffer hats = GLFW.glfwGetJoystickHats(jid);
            if (hats != null) {
                //getEventBus().callEvent();
            }


        }
    }
}
