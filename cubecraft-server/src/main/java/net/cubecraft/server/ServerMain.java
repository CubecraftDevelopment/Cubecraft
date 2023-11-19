package net.cubecraft.server;


import ink.flybird.fcommon.container.StartArguments;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;

public final class ServerMain {
    private static final ILogger LOGGER = LogManager.getLogger("ServerMain");
    public static CubecraftServer server;

    public static void main(String[] args) {
        startArguments =new StartArguments(args);
        gamePath= startArguments.getValueAsString("path",System.getProperty("user.dir"));

        if(server!=null&&server.isRunning()){
            LOGGER.warn("already running server,stopping it!");
            server.setRunning(false);
            LOGGER.info("existing server stopped,now starting new server!");
        }

        //server=new CubecraftServer(addr, levelName);
        Thread thread=new Thread(server);
        thread.setName("server_main");
        thread.start();
    }

    private static String gamePath;

    public static String getGamePath() {
        return gamePath;
    }

    private static StartArguments startArguments;

    public static StartArguments getArgs() {
        return startArguments;
    }
}
