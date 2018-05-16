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

    void loadModules(Bot bot) {
        for (Module module : scanModules()) {
            module.preLoad(bot);
            bot.enableModule(module);
        }
    }

    private static List<Module> scanModules() {
        List<Module> modules = new ArrayList<>();
        System.out.println("Scanning for modules...");
        File moduleDir = new File("modules");
        if (moduleDir.exists()) {
            File[] moduleList = moduleDir.listFiles(file -> file.getPath().toLowerCase().endsWith(".jar"));
            if (moduleList == null) {
                System.out.println("No modules found!\nShutting down...");
                Controller.stop();
                System.exit(0);
                return modules;
            }
            URL[] urls = Arrays.stream(moduleList)
                               .map(file -> {
                                   try {
                                       return file.toURI().toURL();
                                   } catch (MalformedURLException e) {
                                       e.printStackTrace();
                                   }
                                   return null;
                               })
                               .filter(Objects::nonNull)
                               .toArray(URL[]::new);
            System.out.printf("Found %d files.\nLoading file%s...\n", urls.length, urls.length == 1 ? "" : "s");
            URLClassLoader ucl = new URLClassLoader(urls);
            ServiceLoader<Module> loader = ServiceLoader.load(Module.class, ucl);
            for (Module module : loader) {
                System.out.println("Loaded: " + module.getModuleInfo().getName());
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

    void loadModules(Bot bot, HashMap<String, State> modules) {
        for (Module module : scanModules()) {
            if (modules != null) {
                module.initialize(modules.get(module.getModuleInfo().getModuleId()));
            }
            module.preLoad(bot);
            bot.enableModule(module);
        }
    }
}
