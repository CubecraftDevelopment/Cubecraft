package net.cubecraft.resource;


import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;
import me.gb2022.commons.registry.RegisterMap;
import net.cubecraft.SharedContext;
import net.cubecraft.event.resource.ResourceLoadFinishEvent;
import net.cubecraft.event.resource.ResourceLoadItemEvent;
import net.cubecraft.event.resource.ResourceLoadStartEvent;
import net.cubecraft.resource.item.IResource;
import net.cubecraft.resource.provider.InternalResourceLoader;
import net.cubecraft.resource.provider.ModResourceLoader;
import net.cubecraft.resource.provider.ResourceLoader;
import net.cubecraft.util.thread.TaskFutureWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ResourceManager {
    private static final Logger LOGGER = LogManager.getLogger("ResourceManager");

    public final ArrayList<ResourcePack> resourcePacks = new ArrayList<>();
    protected final ArrayList<Object> listeners = new ArrayList<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private final Map<String, ResourceLoader> loaders = new HashMap<>();
    private final SimpleEventBus eventBus = new SimpleEventBus();
    private final HashMap<String, RegisterMap<IResource>> resourceObjectCache = new HashMap<>(512);
    private final Map<String, Set<ResourcePlugin>> plugins = new HashMap<>();

    private final ArrayList<String> namespaces = new ArrayList<>();


    public ResourceManager() {
        this.loaders.put("cubecraft:internal", new InternalResourceLoader());
        this.loaders.put("cubecraft:mod", new ModResourceLoader());
    }

    public SimpleEventBus getEventBus() {
        return this.eventBus;
    }

    //----[register]----
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
        this.resourceObjectCache.computeIfAbsent(loadStage, K -> new RegisterMap<>(IResource.class)).registerItem(namespace, id, resource);
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

    private void loadResource(List<ResourceLoader> loaders, String stage, IResource resource) {
        if (resource == null) {
            return;
        }
        for (ResourceLoader loader : loaders) {
            if (!loader.load(resource)) {
                continue;
            }
            this.eventBus.callEvent(new ResourceLoadItemEvent(stage, resource));
            return;
        }
        LOGGER.warn("failed to load resource:{}", resource.toString());
    }

    public void loadResource(IResource resource) {
        for (ResourceLoader loader : this.getSortedLoaders()) {
            if (!loader.load(resource)) {
                continue;
            }
            return;
        }
    }

    public Set<? extends Future<?>> load(Collection<? extends IResource> resources, String stage, boolean async) {
        var loaders = this.getSortedLoaders();

        if (!async) {
            return resources.stream().map(r -> new TaskFutureWrapper(() -> loadResource(loaders, stage, r))).collect(Collectors.toSet());
        }
        return resources.stream()
                .map((r) -> (Runnable) () -> loadResource(loaders, stage, r))
                .collect(Collectors.toSet())
                .stream()
                .map(this.threadPool::submit)
                .collect(Collectors.toSet());
    }

    public void loadBlocking(Collection<? extends IResource> resources, String stage, boolean async) {
        for (var future : load(resources, stage, async)) {
            try {
                future.get();
            } catch (InterruptedException e) {
                LOGGER.catching(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void load(String stage, boolean async) {
        this.eventBus.callEvent(new ResourceLoadStartEvent(stage));
        var list = this.createLoadList(stage);

        if (list != null) {
            var resources = list.stream().map((s) -> this.resourceObjectCache.get(stage).get(s)).collect(Collectors.toSet());
            this.loadBlocking(resources, stage, async);
        }

        for (var plugin : this.getPlugins(stage)) {
            plugin.onResourceReload(this, stage);
        }

        this.eventBus.callEvent(new ResourceLoadFinishEvent(stage), stage);
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

    public void addNameSpace(String space) {
        if (!this.namespaces.contains(space)) {
            this.namespaces.add(space);
        }
    }

    public List<String> getNameSpaces() {
        return this.namespaces;
    }


    //----[plugin api]----
    public Set<ResourcePlugin> getPlugins(String stage) {
        return this.plugins.computeIfAbsent(stage, k -> new HashSet<>());
    }

    public void addPlugin(String stage, ResourcePlugin plugin) {
        this.getPlugins(stage).add(plugin);
    }

    public void removePlugin(String stage, ResourcePlugin plugin) {
        this.getPlugins(stage).remove(plugin);
    }
}
