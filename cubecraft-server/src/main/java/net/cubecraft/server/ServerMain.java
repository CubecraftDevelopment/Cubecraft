package net.cubecraft.server;


import me.gb2022.commons.container.StartArguments;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ServerMain {
    private static final Logger LOGGER = LogManager.getLogger("ServerMain");
    public static CubecraftServer server;
    private static String gamePath;
    private static StartArguments startArguments;

    public static void main(String[] args) {
        startArguments = new StartArguments(args);
        gamePath = startArguments.getValueAsString("path", System.getProperty("user.dir"));

        if (server != null && server.isRunning()) {
            LOGGER.warn("already running server,stopping it!");
            server.setRunning(false);
            LOGGER.info("existing server stopped,now starting new server!");
        }

        server = CubecraftServer.createExternalServer();
        Thread thread = new Thread(server);
        thread.setName("ServerThread");
        thread.start();
    }

    public static String getGamePath() {
        return gamePath;
    }

    public static StartArguments getArgs() {
        return startArguments;
    }
}
