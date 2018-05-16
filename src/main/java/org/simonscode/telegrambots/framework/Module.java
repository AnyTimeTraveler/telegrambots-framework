package org.simonscode.telegrambots.framework;

import org.telegram.telegrambots.api.objects.Update;

public interface Module {

    /**
     *
     * @return
     */
    ModuleInfo getModuleInfo();

    /**
     * This function is called when you module is first loaded by the framework
     *
     * @param state the state last returned by the save function or null, if no previous state has been found
     */
    void initialize(State state);

    /**
     * This function will be executed just before your module will be loaded by a bot.
     */
    void preLoad(Bot bot);

    /**
     * This function will be executed just after your module was loaded by a bot.
     */
    void postLoad(Bot bot);

    /**
     * This function will be executed when telegram has an update.
     *
     * @param sender is the bot that sent the update
     * @param update is the update from telegram
     */
    void processUpdate(Bot sender, Update update);

    /**
     * This function will be executed just before your module will be unloaded.
     */
    void preUnload(Bot bot);

    /**
     * This function will be executed after your module was unloaded.
     */
    void postUnload(Bot bot);

    /**
     * This method is called when the bot wants you to save.
     *
     * @param bot the bot that wants you to save
     * @return either the new state or null, if you do not wish to save at this moment
     */
    State saveState(Bot bot);

    /**
     * @return the class of your implementation of State
     */
    Class<? extends State> getStateType();
}
