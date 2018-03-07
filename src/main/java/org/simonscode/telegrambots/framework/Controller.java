package org.simonscode.telegrambots.framework;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Controller {
    private static final List<BotSession> sessions = new ArrayList<>();
    private static List<Bot> bots = new ArrayList<>();

    public static void main(String[] args) {
        ApiContextInitializer.init();
        ModuleLoader moduleLoader = new ModuleLoader();
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            Config cfg = Config.getInstance();
            for (BotInfo config : cfg.bots) {
                Bot bot = new Bot(config);
                moduleLoader.loadModules(bot, config.getModuleData());
                bot.updateModules();
                bots.add(bot);
            }
            for (Bot bot : bots) {
                sessions.add(telegramBotsApi.registerBot(bot));
            }
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        Config.getInstance().save();

        Scanner sc = new Scanner(System.in);
        String line;

        input:
        while (true) {
            line = sc.nextLine();
            if (line.isEmpty())
                continue;
            String[] command = line.split(" ");
            switch (command[0]) {
                case "exit":
                case "quit":
                case "q":
                    break input;
                case "join":
                    //TODO: Implement joining a chat
                    break;
                case "toogle":
                    if (command.length == 2) {
                        switch (command[1]) {
                            case "chat":
                                //TODO: Implement tollgle chat log
                        }
                    }
                    break;
                default:
                    System.out.println("Unknown command: " + line);
                    break;
            }
        }
        stop();
    }

    static void stop() {
        System.out.print("Stopping running bots...");
        for (BotSession session : sessions) {
            session.stop();
        }
        System.out.println("Done!");
        System.out.print("Saving states...");
        Config.getInstance().save();
        System.out.println("Done!");
    }
}
