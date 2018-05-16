package org.simonscode.telegrambots.framework;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by simon on 29.04.17.
 */
public class Bot extends TelegramLongPollingBot {
    private final BotInfo botInfo;
    private final Object MODULES_LOCK;
    private final Object MODULE_INFO_LOCK;
    private final Object MODULES_ADD_LOCK;
    private final Object MODULES_REMOVE_LOCK;
    private List<Module> modules;
    private List<Module> modulesToAdd;
    private List<Module> modulesToRemove;
    private List<ModuleInfo> moduleInfos;

    /**
     * For testing, run a bot with a predefined set of modules
     */
    public Bot(String username, String apiKey, List<Module> modules) {
        this(new BotInfo(username, apiKey, new HashMap<>()));
        this.modules.addAll(modules);
    }

    Bot(BotInfo botInfo) {
        this.botInfo = botInfo;
        modules = new ArrayList<>();
        moduleInfos = new ArrayList<>();
        modulesToAdd = new ArrayList<>();
        modulesToRemove = new ArrayList<>();
        MODULES_LOCK = new Object();
        MODULE_INFO_LOCK = new Object();
        MODULES_ADD_LOCK = new Object();
        MODULES_REMOVE_LOCK = new Object();
        System.out.println("Starting Bot: " + botInfo.getName() + "...");
    }

    @Override
    public void onUpdateReceived(Update update) {
        synchronized (MODULES_LOCK) {
            modules.parallelStream().forEach(module -> {
                try {
                    module.processUpdate(this, update);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        updateModules();
    }

    void updateModules() {
        synchronized (MODULES_LOCK) {
            synchronized (MODULE_INFO_LOCK) {
                synchronized (MODULES_REMOVE_LOCK) {
                    modulesToRemove.forEach(it -> it.preUnload(this));
                    modules.removeAll(modulesToRemove);
                    modulesToRemove.stream().map(Module::getModuleInfo).forEach(moduleInfos::remove);
                    modulesToRemove.forEach(m -> m.postUnload(this));
                    modulesToRemove.clear();
                }
                synchronized (MODULES_ADD_LOCK) {
                    modulesToAdd.removeAll(modules); // remove all modules already loaded
                    modules.addAll(modulesToAdd);
                    modulesToAdd.stream().map(Module::getModuleInfo).forEach(moduleInfos::add); // add the newly loaded modules to the new list
                    modulesToAdd.forEach(m -> m.postLoad(this));
                    modulesToAdd.clear();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botInfo.getName();
    }

    @Override
    public String getBotToken() {
        return botInfo.getApiKey();
    }

    @Override
    public void onClosing() {
        synchronized (MODULES_LOCK) {
            for (Module module : modules) {
                botInfo.setModuleState(module.getModuleInfo().getModuleId(), module.saveState(this));
            }
            Config.getInstance().save();
        }
    }

    public void enableModule(Module module) {
        synchronized (MODULES_ADD_LOCK) {
            modulesToAdd.add(module);
        }
    }

    public void disableModule(Module module) {
        synchronized (MODULES_REMOVE_LOCK) {
            if (!modulesToRemove.contains(module)) { // only schedule a module once for unloading
                modulesToRemove.add(module);
            }
        }
    }

    public List<ModuleInfo> getModules() {
        synchronized (MODULE_INFO_LOCK) {
            return moduleInfos;
        }
    }

    public void saveModuleStates() {
        synchronized (MODULES_LOCK) {
            for (Module module : modules) {
                try {
                    State state = module.saveState(this);
                    if (state == null) {
                        continue;
                    }

                    botInfo.getModuleData().put(module.getModuleInfo().getModuleId(), state);
                } catch (Throwable t) {
                    System.err.printf("Module %s threw an error while saving:\n", module.getModuleInfo().getModuleId());
                    t.printStackTrace();
                }
            }
        }
    }
}
