package ink.flybird.cubecraft.extansion;

import ink.flybird.cubecraft.EnvironmentPath;
import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.fcommon.threading.TaskProgressCallback;
import ink.flybird.fcommon.threading.TaskProgressUpdateListener;
import ink.flybird.cubecraft.SharedContext;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;


public class ExtensionManager {
    private final EventBus eventBus = new SimpleEventBus();
    private final Logger logger = new SimpleLogger("ModManager");
    private final HashMap<String, Object> mods = new HashMap<>();
    private final ExtensionInfoMapping modMap = new ExtensionInfoMapping();

    public static ExtensionSide getExtensionSide(Class<?> clazz){
        CubecraftExtension extension=clazz.getAnnotation(CubecraftExtension.class);
        if(extension==null){
            return null;
        }
        return extension.side();
    }

    public static int getExtensionAPIVersion(Class<?> clazz){
        CubecraftExtension extension=clazz.getAnnotation(CubecraftExtension.class);
        if(extension==null){
            return 0;
        }
        return extension.apiVersion();
    }

    public Object getModByID(String modID) {
        return this.mods.get(modID);
    }

    private Properties readModInfo(JarFile file) {
        try {
            Properties props = new Properties();
            props.load(file.getInputStream(file.getEntry("content_mod_info.properties")));
            return props;
        } catch (IOException e) {
            this.logger.exception(e);
        }
        return null;
    }

    public EventBus getModLoaderEventBus() {
        return this.eventBus;
    }

    public HashMap<String, Object> getLoadedMods() {
        return this.mods;
    }

    public void registerInternalMod(String loc) {
        try {
            Properties modProps=new Properties();
            modProps.load(this.getClass().getResourceAsStream(loc));
            Object modObj;
            Class<?> clazz;
            clazz = Class.forName(modProps.getProperty("mod.class"));
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            modObj = constructor.newInstance();
            this.mods.put(modProps.getProperty("mod.id"), modObj);
            this.getModLoaderEventBus().registerEventListener(modObj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * the first step of mod loading:scan mod info.<br/>
     * <p>
     * function will simply return in these cases.
     * <li>mod folder not exist.</li>
     * <li>mod folder is empty</li>
     * <p>
     * also,in these cases loader will create warn and ignore/skip the mod:
     * <li>mod is null.</li>
     * <li>mod has no property.</li>
     * <li>mod has no id in property.</li>
     * <li>exception caught</li>
     * <p>
     * successfully scanned mod will be stored(property,status={@code ModStatus.NOT_INITIALIZED})
     */
    public void scanMods(TaskProgressUpdateListener listener) {
        File modFolder = EnvironmentPath.getModFolder();

        if (!modFolder.exists()) {
            modFolder.mkdirs();
            this.logger.warn("can not find mod path(where is my content pack?)");
            return;
        }

        File[] mods = EnvironmentPath.getModFolder().listFiles();
        if (mods == null) {
            this.logger.warn("can not find any installed mod->no game content.");
            return;
        }


        int counter = 0;
        for (File f : mods) {
            try {
                SharedContext.CLASS_LOADER.addURL(f.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            counter++;
            listener.onProgressChange(counter / mods.length * 100);
            listener.onProgressStageChanged("scanning mod:%s".formatted(f.getName()));
            listener.refreshScreen();
            try {
                Properties modProps = this.readModInfo(new JarFile(f));
                if (modProps == null) {
                    this.logger.error("can nod read content_mod_info.properties for mod:%s,ignored it".formatted(f.getName()));
                    continue;
                }

                String modID = modProps.getProperty("mod.id");
                if (modID == null) {
                    this.logger.error("can nod read id for mod:%s,ignored it".formatted(f.getName()));
                    continue;
                }
                this.modMap.insertMod(modID, f.getName(), modProps);
            } catch (Exception e) {
                this.logger.error("failed to load mod:%s,caused by exception:".formatted(f.getName()), e);
            }
        }
    }

    /**
     * the second step in mod loading:construct.<br/>
     * <p>
     * this step instance all mod-main-class into a listener,and register them on event bus.<br/>
     * <p>
     * in these cases mod will be marked as CONSTRUCTION_FAILED,and will not load in next step:
     *
     * <li>mod does not have 'mod.class' property in 'content_mod_info.properties'</li>
     * <li>find exception finding mod constructor</li>
     * <li>find exception when getting mod class(should not be happened)</li>
     * <li>find exception when invoking mod constructor</li>
     */
    public void constructMods(TaskProgressUpdateListener listener) {
        int counter = 0;
        for (String id : this.modMap.getModIDList()) {
            counter++;
            listener.onProgressChange(counter / this.modMap.getModIDList().size() * 100);
            listener.onProgressStageChanged("constructing mod:%s".formatted(id));
            listener.refreshScreen();
            Properties modProp = this.modMap.getModProperty(id);
            String modClass = modProp.getProperty("mod.class");
            if (modClass == null) {
                this.logger.error("failed to construct mod %s : no 'mod.class' in content_mod_info.properties.".formatted(id));
                this.modMap.modifyStatus(id, ExtensionStatus.CONSTRUCTION_FAILED);
                continue;
            }

            Object modObj;

            try {
                Class<?> clazz = SharedContext.CLASS_LOADER.loadClass(modClass);
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                modObj = constructor.newInstance();
            } catch (Exception e) {
                if (e instanceof ClassNotFoundException) {
                    e.printStackTrace();
                    this.logger.error("failed to construct mod %s : can not load mod class.".formatted(id));
                }
                if (e instanceof NoSuchMethodException) {
                    this.logger.error("failed to construct mod %s : no constructor in mod class.".formatted(id));
                }
                if (e instanceof IllegalAccessException || e instanceof InstantiationException) {
                    this.logger.error("failed to construct mod %s : can not construct mod.".formatted(id));
                }
                if (e instanceof InvocationTargetException) {
                    this.logger.error("failed to construct mod %s : caught exception in construct(%s)".formatted(id, e.getMessage()));
                }
                this.modMap.modifyStatus(id, ExtensionStatus.CONSTRUCTION_FAILED);
                continue;
            }
            this.mods.put(id, modObj);
            this.modMap.modifyStatus(id, ExtensionStatus.CONSTRUCTED);
        }
    }

    /**
     * a step before mod initialization.<br/>
     * <p>
     * give all mods a priority,when a modA reference another modB,modB will add a priority point,also register them into mod loader eventbus<br/>
     * <p>
     * return a mod list (sorted by priority),most referenced mod will be front.<br/>
     * <p>
     * if a mod isn`t constructed this step will ignore the mod.
     */
    public void sortAndRegisterMods() {
        Set<String> idList = new HashSet<>();
        for (String id : this.modMap.getModIDList()) {
            if (modMap.getModStatus(id) != ExtensionStatus.CONSTRUCTED) {
                continue;
            }
            Properties prop = this.modMap.getModProperty(id);
            if(prop==null){
                return;
            }
            String dependLine = prop.getProperty("mod.dependencies");
            if (dependLine == null) {
                if (idList.contains(id)) {
                    continue;
                }
                idList.add(id);
                this.getModLoaderEventBus().registerEventListener(this.mods.get(id));
                continue;
            }
            String[] dependencies = dependLine.split(",");
            for (String dep : dependencies) {
                if (idList.contains(dep)) {
                    continue;
                }
                idList.add(dep);
                this.getModLoaderEventBus().registerEventListener(this.mods.get(dep));
            }
            if (idList.contains(id)) {
                continue;
            }
            idList.add(id);
            this.getModLoaderEventBus().registerEventListener(this.mods.get(id));
        }
    }

    /**
     * the combination of mod loading-construct-sort and register.
     *
     * @param listener the task listener.
     */
    public void loadMods(TaskProgressUpdateListener listener) {
        this.logger.info("starting mod loading...");
        long last = System.currentTimeMillis();
        this.logger.info("scanning mods...");
        SharedContext.MOD.scanMods(listener);
        this.logger.info("construction mods...");
        SharedContext.MOD.constructMods(listener);
        this.logger.info("registering mods...");
        this.sortAndRegisterMods();
        this.logger.info("mod loading done:%d ms".formatted(System.currentTimeMillis() - last));
    }

    public void initialize(TaskProgressCallback listener, ExtensionInitializationOperation[] operations) {
        for (ExtensionInitializationOperation operation : operations) {
            long last = System.currentTimeMillis();
            this.logger.info("%s mods...".formatted(operation.getName()));
            this.getModLoaderEventBus().callEvent(operation.createEvent());
            this.logger.info("%s mods finished,in %d ms".formatted(operation.getName(), System.currentTimeMillis() - last));
        }
    }

    public void initializeClientSide(){}


}
