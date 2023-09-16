package ink.flybird.cubecraft.server;

import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.fcommon.container.StartArguments;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;

public class StartServer {
    public static CubecraftServer server;

    public static void main(String[] args) {

        Logger logHandler= new SimpleLogger("ServerMain");
        //init game runtime
        startArguments =new StartArguments(args);
        gamePath= startArguments.getValueAsString("path",System.getProperty("user.dir"));

        //start thread
        if(server!=null&&server.isRunning()){
            logHandler.warn("already running server,stopping it!");
            server.stop();
            logHandler.info("existing server stopped,now starting new server!");
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
