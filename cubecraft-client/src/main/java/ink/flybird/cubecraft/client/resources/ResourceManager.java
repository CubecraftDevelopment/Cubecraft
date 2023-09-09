package ink.flybird.cubecraft.client.resources;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.event.ClientResourceReloadEvent;
import ink.flybird.cubecraft.client.resources.event.ResourceLoadStartEvent;
import ink.flybird.cubecraft.client.resources.resource.IResource;
import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;
import ink.flybird.fcommon.registry.RegisterMap;
import io.flybird.cubecraft.register.SharedContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;

public class ResourceManager {
    public final ArrayList<ResourcePack> resourcePacks = new ArrayList<>();
    public final Logger logHandler = new SimpleLogger("ResourceManager");
    protected final ArrayList<Object> listeners = new ArrayList<>();
    private final HashMap<String, RegisterMap<IResource, ?>> resourceObjectCache = new HashMap<>(512);
    private final EventBus eventBus = new SimpleEventBus();
    private final ArrayList<String> namespaces = new ArrayList<>();
    private final HashMap<String, IResource> resourcePool = new HashMap<>();

    private final ExecutorService threadPool= Executors.newFixedThreadPool(4);

    public void registerResource(IResource resource, Class<?> clazz) {

        Load loadAnnotation = resource.getClass().getAnnotation(Load.class);
        String loadStage = loadAnnotation == null ? "default" : loadAnnotation.value();

        FieldRegistry registryAnnotation = resource.getClass().getAnnotation(FieldRegistry.class);
        String namespace = registryAnnotation.namespace();
        if (Objects.equals(namespace, FieldRegistry.DEFAULT_NAMESPACE)) {
            namespace = clazz.getAnnotation(FieldRegistryHolder.class).value();
        }

        this.resourceObjectCache.computeIfAbsent(loadStage, K -> new RegisterMap<>())
                .registerItem(namespace, registryAnnotation.value(), resource);
    }

    public void registerResources(Class<?> clazz) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getAnnotation(FieldRegistry.class) == null) {
                continue;
            }
            f.setAccessible(true);
            try {
                this.registerResource((IResource) f.get(null), clazz);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void load(String stage) {
        this.eventBus.callEvent(new ResourceLoadStartEvent());
        AtomicInteger counter = new AtomicInteger(0);
        Collection<IResource> resources = this.resourceObjectCache.get(stage).itemList();
        if(resources==null){
            return;
        }
        for (IResource resource:resources){
            this.threadPool.submit(() -> {
                load(resource);
            });
        }
    }

    public void load(IResource resource){
        //todo
    }









    /**
     * create a resource,register it in pool for multiple loading.
     *
     * @param namespace    namespace of resource.
     * @param relativePath relative location
     * @param clazz        class of resource.
     * @param <T>          class of resource.
     * @return loaded resource
     */
    public <T extends IResource> T getResource(String namespace, String relativePath, Class<T> clazz) {
        String resID = IResource.getID(namespace, relativePath, clazz);
        IResource res = this.resourcePool.get(resID);
        if (res != null) {
            return clazz.cast(res);
        }
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(String.class, String.class);
            res = constructor.newInstance(namespace, relativePath);
            this.addResource(resID, res);
            return clazz.cast(this.loadResource(res));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addResource(String resID, IResource res) {
        this.resourcePool.put(resID, res);
    }

    public <T extends IResource> T registerResource(String resID, T res) {
        this.resourcePool.put(resID, res);
        return this.loadResource(res);
    }


    /**
     * load resource,try to get input from source by priority
     *
     * @param resource resource that has to be loaded
     * @param <T>      class of resource
     * @return loaded resource
     */
    public <T extends IResource> T loadResource(T resource) {
        assert resource != null;

        String path = resource.getAbsolutePath();
        InputStream inputStream = null;
        for (ResourcePack resourcePack : resourcePacks) {
            try {
                inputStream = resourcePack.getInput(path);
            } catch (IOException e) {
                this.logHandler.warn("can not read file:" + path + ",because of " + e);
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
            this.logHandler.warn("failed to load resource:" + path);
            return null;
        }
        try {
            resource.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resource;
    }


    public void reload(CubecraftClient client) {
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.DETECT);
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.BLOCK_MODEL);
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.BLOCK_TEXTURE);
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.COLOR_MAP);
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.FONT_TEXTURE);
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.LANGUAGE);
        this.reloadStage(new ClientResourceReloadEvent(client, this), ResourceLoadStage.UI_CONTROLLER);
    }

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

    public Resource getResource(ResourceLocation resourceLocation) {
        return getResource(resourceLocation.format());
    }

    public void registerEventListener(Object listener) {
        this.eventBus.registerEventListener(listener);
        this.listeners.add(listener);
    }

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

    public void addNameSpace(String space) {
        if (!this.namespaces.contains(space)) {
            this.namespaces.add(space);
        }
    }

    public List<String> getNameSpaces() {
        return this.namespaces;
    }


}
