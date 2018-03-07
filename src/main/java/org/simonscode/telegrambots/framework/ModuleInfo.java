package org.simonscode.telegrambots.framework;

/**
 * Created by simon on 17.07.17.
 */
public class ModuleInfo {
    private String name;
    private String version;
    private State state;

    public ModuleInfo(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
