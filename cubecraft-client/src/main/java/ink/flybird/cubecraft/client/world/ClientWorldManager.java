package ink.flybird.cubecraft.client.world;

import ink.flybird.cubecraft.EnvironmentPath;
import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.event.gui.component.ButtonClickedEvent;
import ink.flybird.cubecraft.client.event.gui.component.ComponentInitializeEvent;
import ink.flybird.cubecraft.client.event.gui.component.TextBarSubmitEvent;
import ink.flybird.cubecraft.client.event.gui.component.ToggleButtonClickedEvent;
import ink.flybird.cubecraft.client.gui.base.Text;
import ink.flybird.cubecraft.client.gui.font.FontAlignment;
import ink.flybird.cubecraft.client.gui.layout.OriginLayout;
import ink.flybird.cubecraft.client.gui.node.*;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.internal.gui.ScreenType;
import ink.flybird.cubecraft.client.net.ClientIO;
import ink.flybird.cubecraft.client.net.ClientNetHandler;
import ink.flybird.cubecraft.internal.network.packet.connect.PacketServerStatusQuery;
import ink.flybird.cubecraft.internal.network.packet.connect.PacketServerStatusQueryResult;
import ink.flybird.cubecraft.level.Level;
import ink.flybird.cubecraft.level.LevelInfo;
import ink.flybird.cubecraft.net.NetHandlerContext;
import ink.flybird.cubecraft.net.packet.PacketListener;
import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.cubecraft.server.ServerFactory;
import ink.flybird.cubecraft.server.ServerStatus;
import ink.flybird.fcommon.NetworkUtil;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.event.SubscribedEvent;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;

import java.io.File;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ClientWorldManager extends ClientNetHandler {
    private static final ILogger LOGGER = LogManager.getLogger("client-world-manager");

    private final CubecraftClient client;
    private final HashMap<String, LevelInfo> levels = new HashMap<>();
    private final ArrayList<LevelInfo> showedInfoList = new ArrayList<>();
    private ServerStatus status;
    private CubecraftServer server;
    private InetSocketAddress integratedServerLocation;
    private LevelInfo selectedLevel;

    private ScrollPanel panel;
    private ToggleButton card;
    private Button joinWorldButton;
    private Button deleteWorldButton;
    private Button editWorldButton;

    public ClientWorldManager(CubecraftClient client) {
        this.client = client;
        this.client.getClientIO().getListener().registerEventListener(this);
        this.client.getGuiManager().getEventBus().registerEventListener(this);
    }

    public void joinLocalWorld(String name) {
        this.server = ServerFactory.createIntegratedServer(name);
        LOGGER.info("integrated server started.");

        new Thread(this.server).start();

        while (!this.server.getLifetimeCounter().isAllocated()) {
            Thread.yield();
        }

        Level level=this.getIntegratedServer().getLevel();
        this.client.joinWorld(level.getLocation(this.client.getPlayer()).getWorld(level));
        level.join(this.client.getPlayer());
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

    public boolean connectToServer(InetSocketAddress address) {
        ClientIO clientIO = this.client.getClientIO();
        if (clientIO.isRunning()) {
            clientIO.closeConnection();
        }

        try {
            clientIO.connect(address);
        } catch (Exception e) {
            return false;
        }
        do {
            clientIO.sendPacket(new PacketServerStatusQuery());
            if (this.waitFor()) {
                return false;
            }
        } while (this.status != ServerStatus.RUNNING);
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
            LevelInfo info = LevelInfo.load(worldFolder);
            String name = info.getLevelName();
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
                this.joinLocalWorld(this.selectedLevel.getLevelName());
            }
            case "create_world" -> {
                LevelInfo info = LevelInfo.create("ExampleWorld", 11451419198710L);
                this.levels.put(info.getLevelName(), info);
            }
        }
    }

    @EventHandler
    public void componentInitialized(ComponentInitializeEvent event) {
        if (event.getComponentID() == null) {
            return;
        }
        switch (event.getComponentID()) {
            }
    }

    @EventHandler
    @SubscribedEvent(ScreenType.SINGLE_PLAYER)
    public void onSubmit(TextBarSubmitEvent event) {
        //this.updateShowedList(event.getText());
    }

    public void updateShowedList(String s) {
        int h = 0;
        this.showedInfoList.clear();
        this.panel.getNodes().clear();

        for (LevelInfo info : this.levels.values()) {
            String name = info.getLevelName();
            if (name.contains(s)) {
                try {
                    ToggleButton panel = (ToggleButton) this.card.cloneComponent();

                    ((OriginLayout) panel.getLayout()).setRelativeY(h);
                    this.panel.addNode(name, panel);
                    ((Label) panel.getNode("title")).setText(new Text(name, 0xFFFFFF, FontAlignment.LEFT));
                    ((Label) panel.getNode("created_time")).setText(new Text(new SimpleDateFormat().format(info.getCreatedTime()), 0xFFFFFF, FontAlignment.LEFT));


                    //((Label) panel.getNode("sub2")).setText(new Text(info.getProperty(LevelInfo.CREATOR), 0xFFFFFF, FontAlignment.LEFT));

                    h += ((OriginLayout) panel.getLayout()).getRelativeHeight();
                    this.showedInfoList.add(this.levels.get(name));
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        }
    }

    @EventHandler
    public void onCardClicked(ToggleButtonClickedEvent event){
        if(event.getComponentID()==null){
            return;
        }
        if(event.isButtonSelected()){
            this.selectedLevel=this.levels.get(event.getComponentID());
            event.getButton().selected=true;
        }

        if(this.selectedLevel!=null){
            this.joinWorldButton.enabled=true;
            this.deleteWorldButton.enabled=true;
            this.editWorldButton.enabled=true;
        }else {
            this.joinWorldButton.enabled = false;
            this.deleteWorldButton.enabled = false;
            this.editWorldButton.enabled = false;
        }
    }

    @EventHandler
    public void initWorldList(ComponentInitializeEvent event) {
        if(event.getComponentID()==null){
            return;
        }
        switch (event.getComponentID()){
            case "world_list"->{
                this.panel = (ScrollPanel) event.getComponent();
                this.card = (ToggleButton) this.panel.getNode("card_example");
                this.readLevels();
            }
            case "join_world"->{
                this.joinWorldButton= ((Button) event.getComponent());
                this.joinWorldButton.enabled=false;
            }
            case "delete_world"->{
                this.deleteWorldButton= ((Button) event.getComponent());
                this.deleteWorldButton.enabled=false;
            }
            case "edit_world"->{
                this.editWorldButton= ((Button) event.getComponent());
                this.editWorldButton.enabled=false;
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
