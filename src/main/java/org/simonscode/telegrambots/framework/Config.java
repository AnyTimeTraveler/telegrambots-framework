package org.simonscode.telegrambots.framework;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Config {

    // Configfile name
    private static final String CONFIGFILE = "botconfig.json";
    private static Config instance;
    public ArrayList<BotInfo> bots;

    private Config() {
        bots = new ArrayList<>();
        HashMap<String, ModuleInfo> moduleData = new HashMap<>();
        moduleData.put("exampleModule0", new ModuleInfo("Example Module 0", "1.0.0"));
        moduleData.put("exampleModule1", new ModuleInfo("Example Module 1", "1.0.0"));
        bots.add(new BotInfo("BOT0", "APIKEY HERE", moduleData));
    }

    static Config getInstance() {
        if (instance == null) {
            load(new File(CONFIGFILE));
        }
        return instance;
    }

    static void load(File file) {
        try {
            Gson gson = new GsonBuilder().create();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            instance = gson.fromJson(reader, Config.class);
        } catch (FileNotFoundException e) {
            System.err.println("Config file not found!\nI created one for you as an example.");
            if (instance == null) {
                instance = new Config();
                instance.save();
                System.exit(1);
            }
        } catch (JsonIOException | JsonSyntaxException e) {
            System.err.println("Config file improperly formatted!");
            e.printStackTrace();
        }
    }

    void save() {
        save(new File(CONFIGFILE));
    }

    void save(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonConfig = gson.toJson(this);
        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write(jsonConfig);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}