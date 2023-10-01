package ink.flybird.cubecraft.client.resources;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.event.ClientResourceReloadEvent;
import ink.flybird.cubecraft.client.event.resource.ResourceLoadFinishEvent;
import ink.flybird.cubecraft.client.event.resource.ResourceLoadItemEvent;
import ink.flybird.cubecraft.client.event.resource.ResourceLoadStartEvent;
import ink.flybird.cubecraft.client.resources.provider.InternalResourceLoader;
import ink.flybird.cubecraft.client.resources.provider.ModResourceLoader;
import ink.flybird.cubecraft.client.resources.provider.ResourceLoader;
import ink.flybird.cubecraft.client.resources.item.IResource;
import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;
import ink.flybird.fcommon.registry.RegisterMap;
import ink.flybird.cubecraft.SharedContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourceManager {
    public final Logger logHandler = new SimpleLogger("ResourceManager");
    public final ArrayList<ResourcePack> resourcePacks = new ArrayList<>();
    protected final ArrayList<Object> listeners = new ArrayList<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private final Map<String, ResourceLoader> loaders = new HashMap<>();
    private final EventBus eventBus = new SimpleEventBus();
    private final HashMap<String, RegisterMap<IResource, ?>> resourceObjectCache = new HashMap<>(512);

    private final ArrayList<String> namespaces = new ArrayList<>();


    public ResourceManager() {
        this.loaders.put("cubecraft:internal", new InternalResourceLoader());
        this.loaders.put("cubecraft:mod", new ModResourceLoader());
    }


    public EventBus getEventBus() {
        return this.eventBus;
    }

    public void registerResources(Class<?> clazz) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getAnnotation(FieldRegistry.class) == null) {
                continue;
            }
            this.registerResource(f, clazz);
        }
    }

    public void registerResource(Field f, Class<?> clazz) {
        f.setAccessible(true);

        Load loadAnnotation = f.getAnnotation(Load.class);
        String loadStage = loadAnnotation == null ? "default" : loadAnnotation.value();

        FieldRegistry registryAnnotation = f.getAnnotation(FieldRegistry.class);
        String namespace = registryAnnotation.namespace();
        if (Objects.equals(namespace, FieldRegistry.DEFAULT_NAMESPACE)) {
            namespace = clazz.getAnnotation(FieldRegistryHolder.class).value();
        }

        try {
            registerResource(loadStage,namespace,registryAnnotation.value(), (IResource) f.get(null));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerResource(String loadStage,String resID, IResource resource) {
        this.registerResource(loadStage,resID.split(":")[0],resID.split(":")[1],resource);
    }

    public void registerResource(String loadStage, String namespace, String id, IResource resource) {
        this.resourceObjectCache.computeIfAbsent(loadStage, K -> new RegisterMap<>())
                .registerItem(namespace, id, resource);
    }


    private List<String> createLoadList(String stage) {
        RegisterMap<IResource, ?> map = this.resourceObjectCache.get(stage);
        if (map == null) {
            return null;
        }
        return new ArrayList<>(map.idList());
    }

    private List<ResourceLoader> getSortedLoaders() {
        List<ResourceLoader> loaders = new ArrayList<>(this.loaders.values());
        loaders.sort((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));
        return loaders;
    }

    private void loadResource(List<ResourceLoader> loaders, String stage, IResource resource, String id) {
        for (ResourceLoader loader : loaders) {
            if (!loader.load(resource)) {
                continue;
            }
            this.eventBus.callEvent(new ResourceLoadItemEvent(stage, resource, id));
            return;
        }
    }

    public void loadResource(IResource resource) {
        for (ResourceLoader loader : this.getSortedLoaders()) {
            if (!loader.load(resource)) {
                continue;
            }
            return;
        }
    }

    public void loadAsync(String stage) {
        this.eventBus.callEvent(new ResourceLoadStartEvent(stage));
        List<String> list = this.createLoadList(stage);
        List<ResourceLoader> loaders = this.getSortedLoaders();
        if (list == null) {
            return;
        }
        AtomicInteger counter = new AtomicInteger(0);
        for (String id : list) {
            Runnable task = () -> {
                this.loadResource(loaders, stage, this.resourceObjectCache.get(stage).get(id), id);
                counter.decrementAndGet();
            };
            counter.incrementAndGet();
            this.threadPool.submit(task);
        }
        while (counter.get() > 0) {
            Thread.yield();
        }
        this.eventBus.callEvent(new ResourceLoadFinishEvent(stage),stage);
    }

    public void load(String stage) {
        this.eventBus.callEvent(new ResourceLoadStartEvent(stage));
        List<String> list = this.createLoadList(stage);
        List<ResourceLoader> loaders = this.getSortedLoaders();
        if (list == null) {
            return;
        }
        for (String id : list) {
            this.loadResource(loaders, stage, this.resourceObjectCache.get(stage).get(id), id);
        }
        this.eventBus.callEvent(new ResourceLoadFinishEvent(stage),stage);
    }

    @Deprecated
    public void reload(CubecraftClient client) {
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.DETECT);
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.BLOCK_MODEL);
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.BLOCK_TEXTURE);
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.COLOR_MAP);
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.FONT_TEXTURE);
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.LANGUAGE);
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.UI_CONTROLLER);
    }

    @Deprecated
    public Resource getResource(String path) {
        InputStream inputStream = null;
        for (ResourcePack resourcePack : resourcePacks) {
            try {
                inputStream = resourcePack.getInput(path);
            } catch (IOException e) {
                logHandler.warn("can not read file:" + path + ",because of " + e);
            }
            if (inputStream != null) {
                break;
            }
        }
        if (inputStream == null) {
            inputStream = SharedContext.CLASS_LOADER.getResourceAsStream(path);
        }
        if (inputStream == null) {
            inputStream = this.getClass().getResourceAsStream(path);
        }
        if (inputStream == null) {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        }
        if (inputStream == null) {
            throw new RuntimeException("could not load anyway:" + path);
        }
        return new Resource(path, inputStream);
    }

    @Deprecated
    public Resource getResource(ResourceLocation resourceLocation) {
        return getResource(resourceLocation.format());
    }

    @Deprecated
    public void registerEventListener(Object listener) {
        this.eventBus.registerEventListener(listener);
        this.listeners.add(listener);
    }

    @Deprecated
    public void reloadStage(ClientResourceReloadEvent e, ResourceLoadStage stage) {
        for (Object el : this.listeners) {
            Method[] ms = el.getClass().getMethods();
            for (Method m : ms) {
                if (Arrays.stream(m.getAnnotations()).anyMatch(annotation -> annotation instanceof ResourceLoadHandler)) {
                    ResourceLoadHandler a = m.getAnnotation(ResourceLoadHandler.class);
                    if (m.getParameters()[0].getType() == e.getClass() && a.stage() == stage) {
                        try {
                            m.invoke(el, e);
                        } catch (IllegalAccessException | InvocationTargetException e2) {
                            throw new RuntimeException(e2);
                        }
                    }
                }
            }
        }
    }

    @Deprecated
    public void addNameSpace(String space) {
        if (!this.namespaces.contains(space)) {
            this.namespaces.add(space);
        }
    }

    @Deprecated
    public List<String> getNameSpaces() {
        return this.namespaces;
    }
}
