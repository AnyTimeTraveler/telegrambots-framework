package org.simonscode.telegrambots.framework;

import java.util.HashMap;

/**
 * Created by simon on 14.11.16.
 */
public class BotInfo {
    private String name;
    private String apiKey;
    private HashMap<String, State> moduleData;

    BotInfo(String name, String apiKey, HashMap<String, State> moduleData) {
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
        moduleData.put(module, state);
    }

    public HashMap<String, State> getModuleData() {
        return moduleData;
    }
}
