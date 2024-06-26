package net.cubecraft.client.world;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.event.SubscribedEvent;
import me.gb2022.commons.file.FileUtil;
import me.gb2022.commons.threading.ThreadState;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import net.cubecraft.EnvironmentPath;
import net.cubecraft.SharedObjects;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.event.gui.component.ButtonClickedEvent;
import net.cubecraft.client.event.gui.component.ComponentInitializeEvent;
import net.cubecraft.client.event.gui.component.TextBarSubmitEvent;
import net.cubecraft.client.event.gui.component.ToggleButtonClickedEvent;
import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.gui.layout.OriginLayout;
import net.cubecraft.client.gui.node.Button;
import net.cubecraft.client.gui.node.Label;
import net.cubecraft.client.gui.node.ScrollPanel;
import net.cubecraft.client.gui.node.ToggleButton;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.client.internal.gui.ScreenType;
import net.cubecraft.client.net.ClientNetHandler;
import net.cubecraft.level.Level;
import net.cubecraft.level.LevelInfo;
import net.cubecraft.server.CubecraftServer;
import net.cubecraft.server.ServerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ClientWorldManager extends ClientNetHandler {
    private static final Logger LOGGER = LogManager.getLogger("client-world-manager");
    private final CubecraftClient client;
    private final HashMap<String, LevelInfo> levels = new HashMap<>();
    private final ArrayList<LevelInfo> showedInfoList = new ArrayList<>();
    private CubecraftServer server;
    private LevelInfo selectedLevel;

    private ScrollPanel panel;
    private ToggleButton card;
    private Button joinWorldButton;
    private Button deleteWorldButton;
    private Button editWorldButton;

    public ClientWorldManager(CubecraftClient client) {
        this.client = client;
        this.client.getClientGUIContext().getEventBus().registerEventListener(this);
    }

    public void joinLocalWorld(String name) {
        this.server = ServerFactory.createIntegratedServer(name);
        LOGGER.info("integrated server started.");

        new Thread(this.server).start();

        while (this.server.getState() != ThreadState.INITIALIZED) {
            Thread.yield();
        }

        Level level = this.getIntegratedServer().getLevel();
        this.client.joinLevel(level);
    }

    public void setConnectionScreenText(String statement, String reason, Object... reasonArgs) {
        Screen screen = this.client.getClientGUIContext().getScreen();
        if (!Objects.equals(screen.id, "cubecraft:connection_screen")) {
            throw new IllegalStateException("not the connection screen:" + screen.id);
        }
        Label statementLabel = (Label) screen.getNodes().get("statement");
        Label reasonLabel = (Label) screen.getNodes().get("reason");
        statementLabel.setText(Text.translated(statement));
        reasonLabel.setText(Text.translated(reason, reasonArgs));
    }

    public boolean waitFor() {
        this.client.getClientGUIContext().refreshScreen();
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

    public CubecraftServer getIntegratedServer() {
        return server;
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
            if (info == null) {
                continue;
            }
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
        this.updateShowedList(event.getText());
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
                    ((Label) panel.getNode("title")).setText(name);
                    ((Label) panel.getNode("created_time")).setText(SharedObjects.DATE_FORMAT.format(info.getCreatedTime()));
                    ((Label) panel.getNode("size")).setText(FileUtil.folderSize(info.getFolder()) / 1000f + "MB");

                    h += ((OriginLayout) panel.getLayout()).getRelativeHeight();
                    this.showedInfoList.add(this.levels.get(name));
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        }
    }

    @EventHandler
    public void onCardClicked(ToggleButtonClickedEvent event) {
        if (event.getComponentID() == null) {
            return;
        }
        if (event.isButtonSelected()) {
            this.selectedLevel = this.levels.get(event.getComponentID());
            event.getButton().selected = true;
        }

        if (this.selectedLevel != null) {
            this.joinWorldButton.enabled = true;
            this.deleteWorldButton.enabled = true;
            this.editWorldButton.enabled = true;
        } else {
            this.joinWorldButton.enabled = false;
            this.deleteWorldButton.enabled = false;
            this.editWorldButton.enabled = false;
        }
    }

    @EventHandler
    public void initWorldList(ComponentInitializeEvent event) {
        if (event.getComponentID() == null) {
            return;
        }
        switch (event.getComponentID()) {
            case "world_list" -> {
                this.panel = (ScrollPanel) event.getComponent();
                this.card = (ToggleButton) this.panel.getNode("card_example");
                this.readLevels();
            }
            case "join_world" -> {
                this.joinWorldButton = ((Button) event.getComponent());
                this.joinWorldButton.enabled = false;
            }
            case "delete_world" -> {
                this.deleteWorldButton = ((Button) event.getComponent());
                this.deleteWorldButton.enabled = false;
            }
            case "edit_world" -> {
                this.editWorldButton = ((Button) event.getComponent());
                this.editWorldButton.enabled = false;
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

    public void leaveWorld() {
        this.getIntegratedServer().setRunning(false);
        this.client.leaveLevel();
        while (this.getIntegratedServer().getState() != ThreadState.TERMINATED) {
            Thread.yield();
        }
    }

    public boolean isIntegrated() {
        return this.getIntegratedServer() != null;
    }
}
