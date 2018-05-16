package org.simonscode.telegrambots.framework;

public class ModuleInfo {

    private String name;
    private String version;
    private String author;
    private InstanciationPereference instanciationPereference;

    public ModuleInfo(String name, String version, String author, InstanciationPereference instanciationPereference) {
        this.name = name;
        this.version = version;
        this.author = author;
        this.instanciationPereference = instanciationPereference;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public String getModuleId() {
        return author + ':' + name + ':' + version;
    }

    public InstanciationPereference getInstanciationPereference() {
        return instanciationPereference;
    }

    public enum InstanciationPereference {
        SINGLE_INSTANCE_ACROSS_ALL_BOTS,
        SINGLE_INSTANCE_PER_BOT,
        MULTIPLE_INSTANCES_PER_BOT
    }
}
