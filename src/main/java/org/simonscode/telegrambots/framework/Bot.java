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
    private final Object MODULES_ADD_LOCK;
    private final Object MODULES_REMOVE_LOCK;
    private List<Module> modules;
    private List<Module> modulesToAdd;
    private List<Module> modulesToRemove;

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
        modulesToAdd = new ArrayList<>();
        modulesToRemove = new ArrayList<>();
        MODULES_LOCK = new Object();
        MODULES_ADD_LOCK = new Object();
        MODULES_REMOVE_LOCK = new Object();
        System.out.println("Starting Bot: " + botInfo.getName() + "...");
    }

    @Override
    public void onUpdateReceived(Update update) {
        Utils.logUpdate(update);
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
            synchronized (MODULES_REMOVE_LOCK) {
                modules.removeAll(modulesToRemove);
                modulesToRemove.clear();
            }
            synchronized (MODULES_ADD_LOCK) {
                modules.addAll(modulesToAdd);
                modulesToAdd.clear();
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
                botInfo.setModuleState(module.getName(), module.saveState(this));
            }
            Config.getInstance().save();
        }
    }

    public boolean enableModule(Module module) {
        synchronized (MODULES_LOCK) {
            if (modules.contains(module))
                return false;
            synchronized (MODULES_ADD_LOCK) {
                modulesToAdd.add(module);
            }
            return true;
        }
    }

    public boolean disableModule(Module module) {
        synchronized (MODULES_LOCK) {
            if (!modules.contains(module))
                return false;
            synchronized (MODULES_REMOVE_LOCK) {
                modulesToRemove.add(module);
            }
            return true;
        }
    }

    public void saveModuleStates() {
        synchronized (MODULES_LOCK) {
            for (Module module : modules) {
                try {
                    State state = module.saveState(this);
                    if (state == null) continue;

                    ModuleInfo info = botInfo.getModuleData().getOrDefault(module.getName(), new ModuleInfo(module.getName(), module.getVersion()));
                    info.setState(state);
                    botInfo.getModuleData().put(module.getName(), info);
                } catch (Throwable t) {
                    System.err.printf("Module %s threw an error while saving:\n", module.getName());
                    t.printStackTrace();
                }
            }
        }
    }
}
