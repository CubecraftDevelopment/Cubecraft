package io.flybird.cubecraft.extansion;

import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

public class ExtensionInfoMapping {
    private final HashMap<String, ExtensionStatus> statusMapping = new HashMap<>();
    private final HashMap<String, Properties> propertyMapping = new HashMap<>();
    private final HashMap<String, String> fileMapping = new HashMap<>();

    public void insertMod(String modID, String modFile, Properties modProp) {
        this.statusMapping.put(modID, ExtensionStatus.SCANNED);
        this.fileMapping.put(modID, modFile);
        this.propertyMapping.put(modID, modProp);
    }

    public Properties getModProperty(String id){
        return this.propertyMapping.get(id);
    }

    public String getModFile(String id){
        return this.fileMapping.get(id);
    }

    public ExtensionStatus getModStatus(String id){
        return this.statusMapping.get(id);
    }

    public void modifyStatus(String id, ExtensionStatus newStatus){
        if(!this.statusMapping.containsKey(id)){
            return;
        }
        this.statusMapping.put(id,newStatus);
    }

    public Set<String> getModIDList(){
        return this.propertyMapping.keySet();
    }

}
