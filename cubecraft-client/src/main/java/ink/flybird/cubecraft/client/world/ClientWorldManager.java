package ink.flybird.cubecraft.client.world;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.gui.base.Text;
import ink.flybird.cubecraft.client.gui.font.FontAlignment;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.internal.gui.component.Button;
import ink.flybird.cubecraft.client.internal.gui.component.CardPanel;
import ink.flybird.cubecraft.client.internal.gui.component.Label;
import ink.flybird.cubecraft.client.internal.gui.container.ScrollPanel;
import ink.flybird.cubecraft.client.internal.gui.event.ButtonClickedEvent;
import ink.flybird.cubecraft.client.event.gui.ComponentInitializeEvent;
import ink.flybird.cubecraft.client.internal.gui.event.TextBarSubmitEvent;
import ink.flybird.cubecraft.client.internal.gui.layout.OriginLayout;
import ink.flybird.cubecraft.client.internal.gui.screen.ScreenType;
import ink.flybird.cubecraft.client.net.ClientIO;
import ink.flybird.cubecraft.client.net.ClientNetHandler;
import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.cubecraft.server.ServerStatus;
import ink.flybird.cubecraft.server.world.ServerWorld;
import ink.flybird.fcommon.NetworkUtil;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.event.SubscribedEvent;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.cubecraft.internal.network.packet.connect.PacketServerStatusQuery;
import ink.flybird.cubecraft.internal.network.packet.connect.PacketServerStatusQueryResult;
import ink.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinRequest;
import ink.flybird.cubecraft.internal.world.WorldType;
import ink.flybird.cubecraft.level.Level;
import ink.flybird.cubecraft.level.LevelInfo;
import ink.flybird.cubecraft.level.LevelInfoFactory;
import ink.flybird.cubecraft.net.NetHandlerContext;
import ink.flybird.cubecraft.net.packet.PacketListener;
import ink.flybird.cubecraft.register.EnvironmentPath;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ClientWorldManager extends ClientNetHandler {
    private final CubecraftClient client;
    private final HashMap<String, LevelInfo> levels = new HashMap<>();
    private final Logger logger = new SimpleLogger("ClientWorldManager");
    private ServerStatus status;
    private CubecraftServer server;
    private InetSocketAddress integratedServerLocation;
    private LevelInfo selectedLevel;
    private final ArrayList<LevelInfo> showedInfoList = new ArrayList<>();
    private ScrollPanel panel;

    public ClientWorldManager(CubecraftClient client) {
        this.client = client;
        this.client.getClientIO().getListener().registerEventListener(this);
        this.client.getGuiManager().getEventBus().registerEventListener(this);
    }

    public boolean checkLocalWorldExist(String name) {
        File[] files = EnvironmentPath.getSaveFolder().listFiles();
        if (files == null || files.length == 0) {
            return false;
        }
        ArrayList<String> s = new ArrayList<>(256);
        for (File f : files) {
            s.add(f.getName());
        }
        return s.contains(name);
    }

    public boolean startIntegratedServer(String worldName) {
        if (!this.checkLocalWorldExist(worldName)) {
            return false;
        }
        this.integratedServerLocation = NetworkUtil.allocateLocalAddressUDP();
        if (this.server.isRunning()) {
            this.server.setRunning(false);
        }
        this.server = new CubecraftServer(this.integratedServerLocation, worldName);
        new Thread(this.server, "integrated_server").start();
        do {
            if (this.waitFor()) {
                return false;
            }
        } while (this.server.getStatus() != ServerStatus.NETWORK_STARTING);
        return true;
    }

    public boolean connectToServer(InetSocketAddress address) {
        this.setConnectionScreenText(
                "connection.statement.connecting",
                "connection.detail.connecting"
        );
        ClientIO clientIO = this.client.getClientIO();
        if (clientIO.isRunning()) {
            clientIO.closeConnection();
        }

        try {
            clientIO.connect(address);
        } catch (Exception e) {
            this.setConnectionScreenText(
                    "connection.statement.failed",
                    "connection.detail.failed_client",
                    e.getMessage()
            );
            return false;
        }
        do {
            this.setConnectionScreenText(
                    "connection.statement.connecting",
                    "connection.server." + status.getStatus()
            );
            clientIO.sendPacket(new PacketServerStatusQuery());
            if (this.waitFor()) {
                return false;
            }
        } while (this.status != ServerStatus.RUNNING);
        return true;
    }

    public boolean joinLocalWorld(String name) {
        if (!this.startIntegratedServer(name)) {
            return false;
        }
        if (!this.connectToServer(this.integratedServerLocation)) {
            return false;
        }
        this.sendPacket(new PacketPlayerJoinRequest(CubecraftClient.CLIENT.getPlayer().getSession()));
        return true;
    }

    public void setConnectionScreenText(String statement, String reason, Object... reasonArgs) {
        Screen screen = this.client.getGuiManager().getScreen();
        if (!Objects.equals(screen.id, "cubecraft:connection_screen")) {
            throw new IllegalStateException("not the connection screen:" + screen.id);
        }
        Label statementLabel = (Label) screen.getNodes().get("statement");
        Label reasonLabel = (Label) screen.getNodes().get("reason");
        statementLabel.setText(Text.translated(statement));
        reasonLabel.setText(Text.translated(reason, reasonArgs));
    }

    public boolean waitFor() {
        this.client.refreshScreen();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            this.setConnectionScreenText(
                    "connection.statement.failed",
                    "connection.detail.failed_client",
                    e.getMessage()
            );
            return true;
        }
        Thread.yield();
        return false;
    }

    @PacketListener
    public void onServerStatusResponse(PacketServerStatusQueryResult packet, NetHandlerContext context) {
        this.status = ServerStatus.fromID(packet.getStatus());
    }

    public CubecraftServer getIntegratedServer() {
        return server;
    }

    public InetSocketAddress getIntegratedServerLocation() {
        return integratedServerLocation;
    }

    public void readLevels() {
        this.levels.clear();

        File saveFolder = EnvironmentPath.getSaveFolder();
        if (!saveFolder.exists()) {
            return;
        }
        File[] files = saveFolder.listFiles();
        if (files == null) {
            return;
        }

        for (File worldFolder : files) {
            LevelInfo info = LevelInfoFactory.fromWorldFolder(worldFolder);
            if (!info.getProperties().containsKey(LevelInfo.NAME)) {
                this.logger.exception("no name property for %s, ignored.".formatted(worldFolder));
                continue;
            }
            String name = info.getProperty(LevelInfo.NAME);
            this.levels.put(name, info);
        }
        updateShowedList("");
    }


    @EventHandler
    @SubscribedEvent(ScreenType.SINGLE_PLAYER)
    public void buttonClicked(ButtonClickedEvent event) {
        switch (event.getComponentID()) {
            case "join_world" -> {
                if (this.selectedLevel == null) {
                    return;
                }
                this.joinLocalWorld(this.selectedLevel.getProperty(LevelInfo.NAME));
            }
            case "create_world" ->
                    this.client.joinWorld(new ServerWorld(WorldType.OVERWORLD, new Level(new LevelInfo(), new ClientWorldFactory()), null));
        }
    }

    @EventHandler
    @SubscribedEvent(ScreenType.SINGLE_PLAYER)
    public void componentInitialized(ComponentInitializeEvent event) {
        switch (event.getComponentID()) {
            case "join_world", "delete_world" -> ((Button) event.getComponent()).enabled = (selectedLevel != null);
            case "world_list" -> {
                this.panel = (ScrollPanel) event.getComponent();
                this.readLevels();
            }
        }
    }

    @EventHandler
    @SubscribedEvent(ScreenType.SINGLE_PLAYER)
    public void onSubmit(TextBarSubmitEvent event) {
        this.updateShowedList(event.getText());
    }

    public void updateShowedList(String s) {
        int h = 0;
        this.showedInfoList.clear();
        this.panel.getNodes().clear();
        for (LevelInfo info : this.levels.values()) {
            String name = info.getProperty(LevelInfo.NAME);
            if (name.contains(s)) {
                try {
                    CardPanel panel = new CardPanel();
                    ((OriginLayout) panel.getLayout()).setRelativeY(h);
                    this.panel.addNode(name, panel);
                    ((Label) panel.getNode("title")).setText(new Text(name, 0xFFFFFF, FontAlignment.LEFT));
                    ((Label) panel.getNode("sub1")).setText(new Text(info.getProperty(LevelInfo.DATE), 0xFFFFFF, FontAlignment.LEFT));
                    ((Label) panel.getNode("sub2")).setText(new Text(info.getProperty(LevelInfo.CREATOR), 0xFFFFFF, FontAlignment.LEFT));

                    h += 50;
                    this.showedInfoList.add(this.levels.get(name));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public LevelInfo getSelectedLevel() {
        return selectedLevel;
    }

    public ArrayList<LevelInfo> getShowedInfoList() {
        return showedInfoList;
    }

    public HashMap<String, LevelInfo> getLevels() {
        return levels;
    }
}
