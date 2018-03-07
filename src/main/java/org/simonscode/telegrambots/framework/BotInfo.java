package org.simonscode.telegrambots.framework;

import java.util.HashMap;

/**
 * Created by simon on 14.11.16.
 */
public class BotInfo {
    private String name;
    private String apiKey;
    private HashMap<String, ModuleInfo> moduleData;

    BotInfo(String name, String apiKey, HashMap<String, ModuleInfo> moduleData) {
        this.name = name;
        this.apiKey = apiKey;
        this.moduleData = moduleData;
    }

    public String getName() {
        return name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setModuleState(String module, State state) {
        ModuleInfo moduleInfo = moduleData.get(module);
        if (moduleInfo != null)
            moduleInfo.setState(state);
    }

    public HashMap<String, ModuleInfo> getModuleData() {
        return moduleData;
    }
}
