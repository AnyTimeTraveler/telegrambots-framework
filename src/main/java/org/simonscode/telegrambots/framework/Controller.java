package org.simonscode.telegrambots.framework;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotSession;

import java.util.*;

public class Controller {
    private static final List<BotSession> sessions = new ArrayList<>();
    private static List<Bot> bots = new ArrayList<>();

    private static Map<String, String> availableCommands = new HashMap<>();

    static {
        availableCommands.put("exit / quit", "Stops the framework");
        availableCommands.put("help / ?", "Prints this info message");
        availableCommands.put("join <channel id>", "Join a chat");
        availableCommands.put("leave <channel id>", "Leave a chat");
        availableCommands.put("say <message>", "Sends message to currently joined channel");
        availableCommands.put("toggle chat", "Toggle messages appearing from the chat");
    }

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
            String[] command = line.trim().split(" ");
            switch (command[0].toLowerCase()) {
                case "exit":
                case "quit":
                case "q":
                    break input;
                case "join":
                case "j":
                    //TODO: Implement joining a chat
                    System.err.println("Leaving not yet implemented!");
                    break;
                case "leave":
                case "l":
                    //TODO: Implement leaving a chat
                    System.err.println("Leaving not yet implemented!");
                    break;
                case "toogle":
                    if (command.length == 2) {
                        switch (command[1]) {
                            case "chat":
                                //TODO: Implement tollgle chat log
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case "help":
                case "?":
                    System.out.println("Available commands:");
                    availableCommands.forEach((key, value) -> System.out.printf("\t\t$-20%s ==> %s\n", key, value));
                    System.out.println();
                    break;
                default:
                    System.out.println("Unknown command: " + line);
                    break;
            }
        }
        stop();
    }

    public static void stop() {
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
