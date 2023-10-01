package ink.flybird.cubecraft.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ink.flybird.fcommon.container.StartArguments;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.fcommon.timer.Timer;
import ink.flybird.quantum3d.device.DeviceContext;
import ink.flybird.quantum3d.lwjgl.context.CompactOGLRenderContext;
import ink.flybird.quantum3d.lwjgl.device.GLFWDeviceContext;
import ink.flybird.quantum3d.render.RenderContext;
import ink.flybird.cubecraft.EnvironmentPath;
import ink.flybird.cubecraft.SharedContext;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class ClientMain {
    private static final Logger logger = new SimpleLogger("client_boot");
    private static StartArguments startArguments;

    public static void main(String[] args) {
        EnvironmentPath.allCreateFolder();

        Platform platform = Platform.current();
        releaseNativeLibraries(platform);
        System.setProperty("java.library.path", EnvironmentPath.NATIVE_FOLDER);

        if (!Platform.is64Bit(platform)) {
            logger.warn("32-bit system is not supported, game will exit.");
        }
        logger.info("system: %s", platform.toString());
        logger.info("start args: " + Arrays.toString(args));
        logger.info("runtime path: " + EnvironmentPath.GAME_FOLDER);
        logger.info("native:" + System.getProperty("java.library.path"));

        DeviceContext deviceContext;
        RenderContext renderContext;

        logger.info("contact with cobalt r2 failed");//todo
        logger.info("contact with cobalt r1 failed");//todo
        logger.info("using quantum3d-lwjgl as platform solution");
        deviceContext = new GLFWDeviceContext();
        GLFW.glfwWindowHint(GLFW.GLFW_SCALE_TO_MONITOR, GLFW.GLFW_TRUE);
        renderContext = new CompactOGLRenderContext(3, 1);

        CubecraftClient client = new CubecraftClient(
                deviceContext,
                renderContext,
                new Timer(20.0f)
        );

        logger.info("starting client thread");
        Thread.currentThread().setContextClassLoader(SharedContext.CLASS_LOADER);
        client.run();

        System.exit(0);
    }

    public static void releaseNativeLibraries(Platform plat) {
        JsonObject object = null;
        try {
            InputStream stream = ClientMain.class.getResourceAsStream("/native/native_index.json");
            object = JsonParser.parseString(new String(stream.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
            stream.close();
        } catch (Exception e) {
            logger.exception(e);
            logger.error("could not read native file.");
            return;
        }

        JsonObject repo = object.get("%s/%s".formatted(plat.getName(), plat.getArch())).getAsJsonObject();

        String destPrefix = repo.get("dest_prefix").getAsString();
        String localPrefix = repo.get("local_prefix").getAsString();
        String downloadPrefix = repo.get("download_prefix").getAsString();
        JsonObject files = repo.get("files").getAsJsonObject();


        for (String s : files.asMap().keySet()) {
            String targetPath = EnvironmentPath.NATIVE_FOLDER + destPrefix + s;
            String localPath = "/native" + localPrefix + files.asMap().get(s).getAsString();

            File target = new File(targetPath);

            if (target.exists()) {
                continue;
            }

            try {
                target.getParentFile().mkdirs();
                target.createNewFile();

                InputStream stream = ClientMain.class.getResourceAsStream(localPath);
                if (stream == null) {
                    throw new RuntimeException("could not locate internal resource: " + localPath);
                }
                FileOutputStream outputStream = new FileOutputStream(target);
                outputStream.write(stream.readAllBytes());
                outputStream.close();
                stream.close();
            } catch (Exception e) {
                logger.exception(e);
                logger.error("could not load native library. system will exit");
                System.exit(0);
            }

            logger.info("releasing library:%s -> %s", localPath, targetPath);
        }
    }
}
