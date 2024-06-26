package net.cubecraft.resource;


import net.cubecraft.SharedContext;
import net.cubecraft.event.resource.ResourceLoadFinishEvent;
import net.cubecraft.event.resource.ResourceLoadItemEvent;
import net.cubecraft.event.resource.ResourceLoadStartEvent;
import net.cubecraft.event.resource.ResourceReloadEvent;
import net.cubecraft.resource.item.IResource;
import net.cubecraft.resource.provider.InternalResourceLoader;
import net.cubecraft.resource.provider.ModResourceLoader;
import net.cubecraft.resource.provider.ResourceLoader;
import me.gb2022.commons.event.EventBus;
import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;
import me.gb2022.commons.registry.RegisterMap;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
    private static final Logger LOGGER = LogManager.getLogger("resource_manager");

    public final ArrayList<ResourcePack> resourcePacks = new ArrayList<>();
    protected final ArrayList<Object> listeners = new ArrayList<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private final Map<String, ResourceLoader> loaders = new HashMap<>();
    private final EventBus eventBus = new SimpleEventBus();
    private final HashMap<String, RegisterMap<IResource>> resourceObjectCache = new HashMap<>(512);

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
            registerResource(loadStage, namespace, registryAnnotation.value(), (IResource) f.get(null));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerResource(String loadStage, String resID, IResource resource) {
        this.registerResource(loadStage, resID.split(":")[0], resID.split(":")[1], resource);
    }

    public void registerResource(String loadStage, String namespace, String id, IResource resource) {
        this.resourceObjectCache.computeIfAbsent(loadStage, K -> new RegisterMap<>(IResource.class))
                .registerItem(namespace, id, resource);
    }


    private List<String> createLoadList(String stage) {
        RegisterMap<IResource> map = this.resourceObjectCache.get(stage);
        if (map == null) {
            return null;
        }
        return new ArrayList<>(map.keySet());
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
        LOGGER.warn("failed to load resource:" + resource.toString());
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
        this.eventBus.callEvent(new ResourceLoadFinishEvent(stage), stage);
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
        this.eventBus.callEvent(new ResourceLoadFinishEvent(stage), stage);
    }

    @Deprecated
    public void reload() {
        this.reloadStage(new ResourceReloadEvent(this), ResourceLoadStage.DETECT);
        this.reloadStage(new ResourceReloadEvent(this), ResourceLoadStage.BLOCK_MODEL);
        this.reloadStage(new ResourceReloadEvent(this), ResourceLoadStage.BLOCK_TEXTURE);
        this.reloadStage(new ResourceReloadEvent(this), ResourceLoadStage.COLOR_MAP);
        this.reloadStage(new ResourceReloadEvent(this), ResourceLoadStage.FONT_TEXTURE);
        this.reloadStage(new ResourceReloadEvent(this), ResourceLoadStage.LANGUAGE);
        this.reloadStage(new ResourceReloadEvent(this), ResourceLoadStage.UI_CONTROLLER);
    }

    @Deprecated
    public Resource getResource(String path) {
        InputStream inputStream = null;
        for (ResourcePack resourcePack : resourcePacks) {
            try {
                inputStream = resourcePack.getInput(path);
            } catch (IOException e) {
                LOGGER.warn("can not read file:" + path + ",because of " + e);
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
    public void reloadStage(ResourceReloadEvent e, ResourceLoadStage stage) {
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
