package net.cubecraft.client.context;

import com.google.gson.JsonObject;
import me.gb2022.commons.container.CollectionUtil;
import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.commons.file.XmlReader;
import me.gb2022.commons.memory.BufferAllocator;
import me.gb2022.commons.registry.ConstructingMap;
import me.gb2022.commons.registry.ItemRegisterEvent;
import me.gb2022.commons.threading.TaskProgressUpdateListener;
import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.device.event.MousePosEvent;
import me.gb2022.quantum3d.device.listener.WindowListener;
import me.gb2022.quantum3d.lwjgl.FrameBuffer;
import me.gb2022.quantum3d.memory.LWJGLBufferAllocator;
import me.gb2022.quantum3d.render.ShapeRenderer;
import me.gb2022.quantum3d.render.vertex.*;
import me.gb2022.quantum3d.util.GLUtil;
import net.cubecraft.SharedContext;
import net.cubecraft.client.ClientContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.event.gui.context.GUIContextInitEvent;
import net.cubecraft.client.gui.ComponentRenderer;
import net.cubecraft.client.gui.ScreenUtil;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.gui.font.FontRenderer;
import net.cubecraft.client.gui.layout.Layout;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.gui.screen.*;
import net.cubecraft.client.registry.ClientSettings;
import net.cubecraft.client.render.gui.ComponentRendererPart;
import net.cubecraft.client.resource.ModelAsset;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.event.resource.ResourceLoadStartEvent;
import net.cubecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public final class ClientGUIContext extends net.cubecraft.client.context.ClientContext implements TaskProgressUpdateListener {
    public static final BufferAllocator BUFFER_ALLOCATOR = new LWJGLBufferAllocator();
    public static final VertexBuilderAllocator BUILDER_ALLOCATOR = new VertexBuilderAllocator(BUFFER_ALLOCATOR);

    public static final ConstructingMap<Node> NODE = new ConstructingMap<>(Node.class);
    public static final ConstructingMap<Layout> LAYOUT = new ConstructingMap<>(Layout.class);
    public static final ConstructingMap<ComponentRendererPart> COMPONENT_RENDERER_PART = new ConstructingMap<>(
            ComponentRendererPart.class,
            JsonObject.class
    );

    public static final HashMap<Class<? extends Node>, ComponentRenderer> COMPONENT_RENDERER = new HashMap<>();

    static {
        NODE.getEventBus().registerEventListener(RegisterListener.class);
        SharedContext.GSON_BUILDER.registerTypeAdapter(ComponentRenderer.class, new ComponentRenderer.JDeserializer());
        SharedContext.GSON_BUILDER.registerTypeAdapter(ComponentRendererPart.class, new ComponentRendererPart.JDeserializer());
    }

    private final CubecraftClient client;
    private final Window window;
    private final SimpleEventBus eventBus = new SimpleEventBus();
    private final FrameBuffer buffer = new FrameBuffer();
    private Screen screen;
    private Screen hoverScreen;
    private float alpha;
    private int animateStatus = 0;
    private boolean checked;
    private int fixedMouseX, fixedMouseY;
    private LogoLoadingScreen loadingScreen;

    public ClientGUIContext(CubecraftClient client, Window window) {
        super(client);
        XmlReader deserializer = SharedContext.FAML_READER;
        this.client = client;
        this.window = window;
        this.client.getDeviceEventBus().registerEventListener(this);
        deserializer.registerDeserializer(Text.class, new Text.XMLDeserializer());
        deserializer.registerDeserializer(Layout.class, new Layout.XMLDeserializer());
        net.cubecraft.client.ClientContext.RESOURCE_MANAGER.getEventBus().registerEventListener(this);
    }

    @Override
    public void init() {
        this.client.getClientEventBus().callEvent(new GUIContextInitEvent(this.client, this));
        this.loadingScreen = new LogoLoadingScreen();

        CubecraftClient.getInstance().getWindow().addListener(new WindowListener() {
            @Override
            public void onSizeEvent(Window window, int width, int height) {
                FontRenderer.ttf().resize();
                FontRenderer.icon().resize();
            }
        });
    }

    public void tick() {
        if (this.screen != null) {
            this.screen.tick();
        }
        if (this.hoverScreen != null && alpha > 0) {
            this.hoverScreen.tick();
        }
    }

    public void drawScreenBuffer(DisplayScreenInfo info) {
        VertexBuilder builder = BUILDER_ALLOCATOR.create(VertexFormat.V3F_C4F_T2F, DrawMode.QUADS, 8);
        builder.allocate();

        GLUtil.enableBlend();
        builder.setColor(1, 1, 1, 1);
        //this.buffer.bindRead();
        ShapeRenderer.drawRectUV(builder, 0, info.getScreenWidth(), 0, info.getScreenHeight(), 0, 0, 1, 1, 0);
        VertexBuilderUploader.uploadPointer(builder);
        builder.free();
        //this.buffer.unbindRead();
    }

    public void renderScreen(DisplayScreenInfo info, float delta) {
        GLUtil.disableDepthTest();
        if (this.screen != null) {
            this.screen.render(info, delta, 1);
        }


        if (this.hoverScreen != null) {
            if (alpha > 0 && animateStatus == -1) {
                alpha -= 0.02F;
            }
            if (alpha < 0 && animateStatus == 1) {
                alpha += 0.02F;
            }
            if (alpha >= 1 && checked) {
                animateStatus = 0;
                checked = false;
            }
            if (alpha > 0) {
                this.hoverScreen.render(info, delta, alpha);
            }
        }

        GLUtil.enableDepthTest();
        ScreenUtil.renderToasts(info, delta);
    }

    public void render(DisplayScreenInfo info, float delta) {

        //this.buffer.resize(w, h);
        //this.buffer.resizeRenderToScaledScreen(0.5f);

        GLUtil.disableDepthTest();
        if (this.screen != null) {
            this.renderScreen(info, delta);

            if (this.screen.getBackgroundType().shouldRenderWorld()) {
                FrameBuffer.bindNone();
                this.renderScreen(info, delta);

                //this.buffer.blit();
                //if (this.renderCount >= ClientSettings.GUI_RENDER_INTERVAL_COUNT.getValue()) {
                //this.buffer.upload(() -> this.renderScreen(info, delta));
                //this.renderCount = 0;
                //}
            } else {
                //FrameBuffer.bindNone();
                this.renderScreen(info, delta);
            }
        }
    }

    //hover control
    public void swapLayer() {
        Screen screen = this.screen;
        this.screen = hoverScreen;
        this.hoverScreen = screen;
    }

    public void disposeHoverScreen() {
        alpha = 1;
        animateStatus = -1;
    }

    public void introHoverScreen() {
        alpha = 0;
        animateStatus = 1;
        checked = true;
    }

    public void displayHoverScreen() {
        alpha = 1;
        animateStatus = 0;
    }

    public float getHoverScreenAlpha() {
        return alpha;
    }

    //screen
    public Screen getScreen() {
        return screen;
    }

    public void setScreen(ScreenBuilder builder) {
        this.setScreen(builder.build());
    }

    public void setScreen(Screen screen) {
        if (this.screen != null) {
            this.screen.release();
        }
        this.screen = screen;
        this.screen.init();
    }

    public Screen getHoverScreen() {
        return hoverScreen;
    }

    public void setHoverScreen(Screen screen) {
        if (this.hoverScreen != null) {
            this.hoverScreen.release();
        }
        this.hoverScreen = screen;
        this.hoverScreen.init();
    }

    public SimpleEventBus getEventBus() {
        return eventBus;
    }

    public Window getWindow() {
        return window;
    }

    public int getFixedMouseX() {
        return fixedMouseX;
    }

    public int getFixedMouseY() {
        return fixedMouseY;
    }

    @EventHandler
    public void onMousePos(MousePosEvent e) {
        double scale = ClientSettings.UISetting.getFixedGUIScale();
        this.fixedMouseX = (int) (e.getX() / scale);
        this.fixedMouseY = (int) (e.getY() / scale);
    }

    @EventHandler
    public void onResourceReload(ResourceLoadStartEvent event) {
        Set<TextureAsset> locations = new HashSet<>();
        CollectionUtil.iterateMap(COMPONENT_RENDERER, ((key, item) -> {
            if (item == null) {
                return;
            }
            item.initializeModel(locations);
        }));
        for (TextureAsset resource : locations) {
            //todo:delegate resource
            ClientContext.RESOURCE_MANAGER.loadResource(resource);
            net.cubecraft.client.ClientContext.TEXTURE.createTexture2D(resource, false, false);
        }
        if (Objects.equals(event.getStage(), "client:startup")) {
            this.loadingScreen = new LogoLoadingScreen();
        }
    }

    public SimpleEventBus getDeviceEventBus() {
        return this.client.getDeviceEventBus();
    }

    @Override
    public void joinWorld(World world) {
        this.setScreen(new HUDScreen());
    }

    public void setHoverLoadingScreen() {
        this.setHoverScreen(this.loadingScreen);
    }

    public void renderAnimationLoadingScreen() {
        this.renderAnimationScreen(this.loadingScreen);
    }

    public void renderAnimationScreen(AnimationScreen screen) {
        this.setScreen(screen);
        while (screen.isAnimationNotCompleted()) {
            this.client.render();
            this.getWindow().update();
            Thread.yield();
        }
    }

    public LogoLoadingScreen getLoadingScreen() {
        return this.loadingScreen;
    }


    @Override
    public void onProgressChange(int progress) {
        this.loadingScreen.updateProgress(progress / 100f);
    }

    @Override
    public void onProgressStageChanged(String newStage) {
        this.loadingScreen.setText(newStage);
    }

    @Override
    public void refreshScreen() {
        this.client.shortTick();
        if (System.currentTimeMillis() % 5 == 0) {
            this.tick();
        }
    }

    public FrameBuffer getRenderBatch() {
        return this.buffer;
    }

    private interface RegisterListener {
        @SuppressWarnings({"ClassGetClass", "unchecked"})
        @EventHandler
        static void onComponentRegister(ItemRegisterEvent event) {
            if (!event.isMap(NODE)) {
                return;
            }
            String id = event.getId();

            ModelAsset asset = new ModelAsset("cubecraft:/ui/" + id + ".json");
            String resID = "cubecraft:" + id + "_render_controller";
            net.cubecraft.client.ClientContext.RESOURCE_MANAGER.registerResource("default", resID, asset);
            net.cubecraft.client.ClientContext.RESOURCE_MANAGER.loadResource(asset);
            ComponentRenderer renderer = SharedContext.createJsonReader().fromJson(asset.getRawText(), ComponentRenderer.class);
            COMPONENT_RENDERER.put(event.getItem(Node.class.getClass()), renderer);
        }
    }
}