package org.simonscode.telegrambots.framework;

public abstract class ModuleAdapter implements Module {

    @Override
    public void initialize(State state) {
    }

    @Override
    public void preLoad(Bot bot) {
    }

    @Override
    public void postLoad(Bot bot) {
    }

    @Override
    public void preUnload(Bot bot) {
    }

    @Override
    public void postUnload(Bot bot) {
    }

    @Override
    public State saveState(Bot bot) {
        return null;
    }

    @Override
    public Class<? extends State> getStateType() {
        return null;
    }
}
