package net.cubecraft.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.cubecraft.EnvironmentPath;
import net.cubecraft.SharedContext;
import ink.flybird.fcommon.timer.Timer;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.quantum3d.device.DeviceContext;
import ink.flybird.quantum3d.lwjgl.context.CompactOGLRenderContext;
import ink.flybird.quantum3d.lwjgl.device.GLFWDeviceContext;
import ink.flybird.quantum3d.render.RenderContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public final class ClientMain {
    private static final ILogger LOGGER= LogManager.getLogger("client-boot");

    public static void main(String[] args) {



        EnvironmentPath.allCreateFolder();
        Platform platform = Platform.current();
        System.setProperty("java.library.path", EnvironmentPath.NATIVE_FOLDER);

        LOGGER.info("system: ", platform.toString());
        LOGGER.info("start args: " + Arrays.toString(args));
        LOGGER.info("runtime path: " + EnvironmentPath.GAME_FOLDER);
        LOGGER.info("native: " + System.getProperty("java.library.path"));

        if (!Platform.is64Bit(platform)) {
            LOGGER.warn("32-bit platform is not supported.");
        }
        if (!Objects.equals(System.getProperty("file.encoding"), "UTF-8")) {
            LOGGER.warn("current platform is not using UTF-8 for standard I/O.");
        }
        if (!releaseNativeLibraries(platform)) {
            LOGGER.warn("failed to exact native library.");
        }

        PlatformSolution solution=PlatformSolutionProvider.getPlatformSolution();

        Thread.currentThread().setContextClassLoader(SharedContext.CLASS_LOADER);
        CubecraftClient client = new CubecraftClient(
                solution.deviceContext(),
                solution.renderContext(),
                new Timer(20.0f)
        );
        LOGGER.info("client started.");
        client.run();
        LOGGER.info("client thread stopped.");
        System.exit(0);
    }

    public static boolean releaseNativeLibraries(Platform plat) {
        JsonObject object;
        try {
            InputStream stream = ClientMain.class.getResourceAsStream("/native/native_index.json");
            if (stream == null) {
                return false;
            }
            object = JsonParser.parseString(new String(stream.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
            stream.close();
        } catch (Exception e) {
            LOGGER.error(e);
            return false;
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
                LOGGER.error(e);
                LOGGER.error("could not load native library. system will exit");
                System.exit(0);
            }

            LOGGER.info("releasing library:%s -> %s", localPath, targetPath);
        }
        return true;
    }

    static class PlatformSolutionProvider {
        public static PlatformSolution getPlatformSolution() {
            PlatformSolution solution = getCobaltR2PlatformSolution();
            if (solution != null) {
                LOGGER.info("successfully loaded Cobalt-R2 as platform solution.");
                return solution;
            }
            solution = getCobaltR1PlatformSolution();
            if (solution != null) {
                LOGGER.info("successfully loaded Cobalt-R1 as platform solution.");
                return solution;
            }
            solution = getLWJGLPlatformSolution();
            LOGGER.info("successfully loaded Quantum3D-LWJGL as platform solution.");
            return solution;
        }

        static PlatformSolution getCobaltR2PlatformSolution() {
            return null;
        }

        static PlatformSolution getCobaltR1PlatformSolution() {
            return null;
        }

        static PlatformSolution getLWJGLPlatformSolution() {
            return new PlatformSolution(
                    new GLFWDeviceContext(),
                    new CompactOGLRenderContext(3, 1)
            );
        }
    }

    public record PlatformSolution(DeviceContext deviceContext, RenderContext renderContext) {
    }
}
