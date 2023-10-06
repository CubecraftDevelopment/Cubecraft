package ink.flybird.cubecraft.server;

import ink.flybird.cubecraft.EnvironmentPath;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public interface ServerFactory {
    static CubecraftServer createIntegratedServer(String levelName) {
        return new CubecraftServer(new InetSocketAddress("127.0.0.1",11451), levelName, true);
    }

    static CubecraftServer createExternalServer() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(EnvironmentPath.CONFIG_FOLDER + "/server_setting.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String levelName = (String) properties.getOrDefault("level.name", "world");
        String ip = (String) properties.getOrDefault("network.address", "127.0.0.1");
        int port = (int) properties.getOrDefault("network.port", 25585);

        return new CubecraftServer(new InetSocketAddress(ip,port),levelName, false);
    }
}
