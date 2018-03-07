package org.simonscode.telegrambots.framework;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Created by simon on 29.04.17.
 */
public class ModuleLoader {
    private static List<Module> scanModules() {
        List<Module> modules = new ArrayList<>();
        System.out.println("Loading modules...");
        File moduleDir = new File("modules");
        if (moduleDir.exists()) {
            File[] moduleList = moduleDir.listFiles(file -> file.getPath().toLowerCase().endsWith(".jar"));
            if (moduleList == null) {
                System.out.println("No modules found!\nShutting down...");
                Controller.stop();
                System.exit(0);
                return modules;
            }
            URL[] urls = Arrays.stream(moduleList).map(e -> {
                try {
                    return e.toURI().toURL();
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
                return null;
            }).filter(Objects::nonNull).toArray(URL[]::new);
            URLClassLoader ucl = new URLClassLoader(urls);
            ServiceLoader<Module> loader = ServiceLoader.load(Module.class, ucl);
            for (Module module : loader) {
                System.out.println("Loaded: " + module.getName());
                modules.add(module);
            }
        } else {
            if (moduleDir.mkdirs()) {
                System.out.println("Module-Directory has been created.");
            } else {
                System.out.println("Unknown error creating modules directory");
            }
        }
        return modules;
    }

    void loadModules(Bot bot) {
        for (Module module : scanModules()) {
            bot.enableModule(module);
        }
    }

    void loadModules(Bot bot, HashMap<String, ModuleInfo> modules) {
        for (Module module : scanModules()) {
            if (modules != null) {
                ModuleInfo moduleInfo = modules.get(module.getName());
                if (moduleInfo != null) {
                    module.initialize(moduleInfo.getState());
                }
            }
            bot.enableModule(module);
        }
    }
}
