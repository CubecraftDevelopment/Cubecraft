package net.cubecraft.mod;

import me.gb2022.commons.event.EventBus;
import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.commons.threading.TaskProgressUpdateListener;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import net.cubecraft.EnvironmentPath;
import net.cubecraft.SharedContext;
import net.cubecraft.Side;
import net.cubecraft.event.mod.ModConstructEvent;
import net.cubecraft.mod.object.Mod;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.jar.JarFile;


public final class ModManager {
    private static final Logger LOGGER = LogManager.getLogger("ModManager");
    private final SimpleEventBus eventBus = new SimpleEventBus();
    private final ExtensionInfoMapping modMap = new ExtensionInfoMapping();

    private final List<String> modLoadList = new ArrayList<>(128);
    private final HashMap<String, Mod> mods = new HashMap<>();

    private boolean registerCompleted = false;


    public Object getModByID(String modID) {
        return this.mods.get(modID);
    }

    //register
    public void registerMod(Mod mod) {
        if (this.registerCompleted) {
            LOGGER.warn("mod registration after register_completed is unstable and may cause issue.");
        }
        mod.loadDescriptionInfo();
        this.mods.put(mod.getId(), mod);
    }

    public void completeModRegister(Side side) {
        this.registerCompleted = true;
        for (String id : this.mods.keySet()) {
            this.addModToList(side, id);
        }
    }

    private void addModToList(Side side, String id) {
        Mod mod = this.mods.get(id);
        if (mod == null) {
            return;
        }

        if (!side.contains(mod.getSide())) {
            return;
        }
        if (this.modLoadList.contains(id)) {
            this.modLoadList.remove(id);
            this.modLoadList.add(0, id);
        } else {
            this.modLoadList.add(0, id);
        }

        for (String s : mod.getDependencies()) {
            addModToList(side, s);
        }
    }

    //construct
    public void constructMods(TaskProgressUpdateListener listener) {
        int counter = 0;

        for (String id : this.modLoadList) {
            var mod = this.mods.get(id);
            mod.construct();

            var eventBus = this.getModLoaderEventBus();

            eventBus.registerEventListener(this.mods.get(id).getModClass());
            eventBus.registerEventListener(this.mods.get(id).getModObject());

            eventBus.callEvent(new ModConstructEvent(this, mod), id);

            if (listener != null) {
                listener.onProgressChange(counter / this.modLoadList.size() * 100);
                listener.onProgressStageChanged("constructing mod: %s".formatted(id));
                listener.refreshScreen();
            }

            counter++;
        }
    }


    private Properties readModInfo(JarFile file) {
        try {
            Properties props = new Properties();
            props.load(file.getInputStream(file.getEntry("content_mod_info.properties")));
            return props;
        } catch (IOException e) {
            LOGGER.throwing(e);
        }
        return null;
    }

    public EventBus getModLoaderEventBus() {
        return this.eventBus;
    }

    public HashMap<String, Mod> getMods() {
        return mods;
    }

    public void scanMods(@Nullable TaskProgressUpdateListener listener) {
        File modFolder = EnvironmentPath.getModFolder();

        if (!modFolder.exists()) {
            modFolder.mkdirs();
            LOGGER.warn("can not find mod path(where is my content pack?)");
            return;
        }

        File[] mods = EnvironmentPath.getModFolder().listFiles();
        if (mods == null) {
            LOGGER.warn("can not find any installed mod->no game content.");
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
            if (listener != null) {
                listener.onProgressChange(counter / mods.length * 100);
                listener.onProgressStageChanged("scanning mod:%s".formatted(f.getName()));
                listener.refreshScreen();
            }
            try {
                Properties modProps = this.readModInfo(new JarFile(f));
                if (modProps == null) {
                    LOGGER.error("can nod read content_mod_info.properties for mod:%s,ignored it".formatted(f.getName()));
                    continue;
                }

                String modID = modProps.getProperty("mod.id");
                if (modID == null) {
                    LOGGER.error("can nod read id for mod:%s,ignored it".formatted(f.getName()));
                    continue;
                }
                this.modMap.insertMod(modID, f.getName(), modProps);
            } catch (Exception e) {
                LOGGER.error("failed to load mod:%s,caused by exception:".formatted(f.getName()), e);
            }
        }
    }

    /*
    public void constructMods(@Nullable TaskProgressUpdateListener listener) {
        int counter = 0;
        for (String id : this.modMap.getModIDList()) {
            counter++;
            if (listener != null) {
                listener.onProgressChange(counter / this.modMap.getModIDList().size() * 100);
                listener.onProgressStageChanged("constructing mod:%s".formatted(id));
                listener.refreshScreen();
            }
            Properties modProp = this.modMap.getModProperty(id);
            String modClass = modProp.getProperty("mod.class");
            if (modClass == null) {
                LOGGER.error("failed to construct mod %s : no 'mod.class' in content_mod_info.properties.".formatted(id));
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
                    LOGGER.error("failed to construct mod %s : can not load mod class.".formatted(id));
                }
                if (e instanceof NoSuchMethodException) {
                    LOGGER.error("failed to construct mod %s : no constructor in mod class.".formatted(id));
                }
                if (e instanceof IllegalAccessException || e instanceof InstantiationException) {
                    LOGGER.error("failed to construct mod %s : can not construct mod.".formatted(id));
                }
                if (e instanceof InvocationTargetException) {
                    LOGGER.error("failed to construct mod %s : caught exception in construct(%s)".formatted(id, e.getMessage()));
                }
                this.modMap.modifyStatus(id, ExtensionStatus.CONSTRUCTION_FAILED);
                continue;
            }
            this.mods.put(id, modObj);
            this.modMap.modifyStatus(id, ExtensionStatus.CONSTRUCTED);
        }
    }

     */

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
            if (prop == null) {
                return;
            }
            String dependLine = prop.getProperty("mod.dependencies");
            if (dependLine == null) {
                if (idList.contains(id)) {
                    continue;
                }
                idList.add(id);
                this.registerToListener(id);
                continue;
            }
            String[] dependencies = dependLine.split(",");
            for (String dep : dependencies) {
                if (idList.contains(dep)) {
                    continue;
                }
                idList.add(dep);
                this.registerToListener(dep);
            }
            if (idList.contains(id)) {
                continue;
            }
            idList.add(id);
            this.registerToListener(id);
        }
    }

    public void registerToListener(String id) {
        this.getModLoaderEventBus().registerEventListener(this.mods.get(id));
        this.getModLoaderEventBus().registerEventListener(this.mods.get(id).getClass());
    }

    public void loadMods(TaskProgressUpdateListener listener) {
        this.scanMods(listener);
        this.constructMods(listener);
        this.sortAndRegisterMods();
    }

    public void serverSetup() {
    }
}
