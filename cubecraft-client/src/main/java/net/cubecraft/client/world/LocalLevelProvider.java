package net.cubecraft.client.world;

import me.gb2022.commons.threading.ThreadState;
import net.cubecraft.EnvironmentPath;
import net.cubecraft.SharedObjects;
import net.cubecraft.client.ClientComponent;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.gui.UI;
import net.cubecraft.client.gui.event.component.ButtonClickedEvent;
import net.cubecraft.client.gui.event.component.TextInputSubmitEvent;
import net.cubecraft.client.gui.node.ListView;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.gui.node.Panel;
import net.cubecraft.client.gui.node.component.Label;
import net.cubecraft.client.gui.node.control.Button;
import net.cubecraft.client.gui.node.control.TextInput;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.client.internal.plugins.ScreenControllerBinder;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.level.LevelInfo;
import net.cubecraft.server.CubecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

//todo binded UI, ListView

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class LocalLevelProvider extends ClientComponent implements ActiveLevelProvider {
    private static final Logger LOGGER = LogManager.getLogger("ClientWorldManager");

    private final HashMap<String, LevelInfo> levels = new HashMap<>();
    private final ViewModel viewModel = new ViewModel();
    private Optional<LevelInfo> currentLevel = Optional.empty();
    private CubecraftServer server;
    private Node template;

    @Override
    public void clientSetup(CubecraftClient client) {
        client.getComponent(ScreenControllerBinder.class).orElseThrow().registerController("cubecraft:single_player_screen", this);
    }

    @UI(UI.INIT)
    void initialize(Screen screen) {
        UI.Binder.bind(this.viewModel, screen);

        this.readLevels();
        this.template = screen.findNode("_template_worldInfo");

        this.viewModel.worldListSize.getText().translatable().setFormat(this.levels.size());
        this.viewModel.worldList.setRenderer((d, i) -> {
            var node = this.template.copyComponent("_ROOT");

            Optional.ofNullable(node).orElseThrow();

            node.getNodeOptional("_world_name", Label.class).orElseThrow().getText().content(d.getLevelName());
            node.getNodeOptional("_world_open_time", Label.class)
                    .orElseThrow()
                    .getText()
                    .content(SharedObjects.DATE_FORMAT.format(d.getLastPlayTime()));

            ((Button) node).setActionListener((b) -> {
                this.currentLevel = Optional.of(d);
                updateUI();
            });

            return node;
        });

        this.currentLevel = this.levels.values().stream().min(Comparator.comparingLong((l) -> l.getLastPlayTime().getTime()));
        this.updateUI();
        this.updateList();
    }

    @UI
    void search(TextInputSubmitEvent event) {
        this.updateList();
    }

    @UI
    void searchWorld(ButtonClickedEvent event) {
        this.updateList();
    }

    @UI
    void joinWorld(ButtonClickedEvent event) {
        joinLocalWorld(this.currentLevel.orElseThrow());
    }

    @UI
    void createWorld(ButtonClickedEvent event) {

    }

    @UI
    void importWorld(ButtonClickedEvent event) {

    }


    public void readLevels() {
        this.levels.clear();

        var saveFolder = EnvironmentPath.getSaveFolder();
        if (!saveFolder.exists()) {
            return;
        }
        var files = saveFolder.listFiles();
        if (files == null) {
            return;
        }

        for (var worldFolder : files) {
            LevelInfo info;
            try {
                info = LevelInfo.open(worldFolder.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            this.levels.put(info.getFolderPath(), info);
        }
    }

    public void updateList() {
        var search = this.viewModel.search.getText();
        var result = new ArrayList<LevelInfo>();

        for (var level : this.levels.values()) {
            var name = level.getLevelName();

            if (!name.contains(search)) {
                continue;
            }

            result.add(level);
        }

        result.sort(Comparator.comparing(LevelInfo::getLastPlayTime));

        this.viewModel.worldList.setData(result);
    }


    public void updateUI() {
        this.currentLevel.ifPresentOrElse((l) -> {
            this.viewModel.worldInfo.setVisible(true);
            this.viewModel.worldName.getText().content(l.getLevelName());
            this.viewModel.worldFolder.getText().content(l.getFolderPath());
            this.viewModel.worldLastPlayed.getText().content(SharedObjects.DATE_FORMAT.format(l.getLastPlayTime()));
        }, () -> this.viewModel.worldInfo.setVisible(false));
    }

    public void joinLocalWorld(LevelInfo name) {
        if (this.server != null) {
            this.server.setRunning(false);

            while (!(this.server.getState() == ThreadState.TERMINATED || server.getState() == ThreadState.TERMINATING_FAILED)) {
                Thread.yield();
            }
        }
        this.server = CubecraftServer.createIntegratedServer(name);
        LOGGER.info("integrated server started.");

        var t = new Thread(this.server);
        t.setName("Server#" + this.server.hashCode());
        t.setDaemon(true);

        t.start();

        while (this.server.getState() != ThreadState.INITIALIZED) {
            Thread.yield();
        }

        var level = this.server.getLevel();
        this.client.setWorldContext(level.join(new EntityPlayer(level, this.client.getSession())));
        this.client.setActiveLevelProvider(this);
    }

    @Override
    public void leave() {
        if (server == null) {
            return;
        }
        this.getClient().setWorldContext(null);
        server.setRunning(false);

        while (!(server.getState() == ThreadState.TERMINATED || server.getState() == ThreadState.TERMINATING_FAILED)) {
            Thread.yield();
        }

        this.server = null;
    }

    @UI.ViewModel
    private static final class ViewModel {
        TextInput search;
        ListView<LevelInfo> worldList;
        Label worldListSize;

        Panel worldInfo;
        Label worldName;
        Label worldFolder;
        Label worldLastPlayed;
    }
}
