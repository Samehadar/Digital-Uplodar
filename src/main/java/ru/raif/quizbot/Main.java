package ru.raif.quizbot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Telegram API bot");
        Config config = config(args);

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

//        var bot = new MyAmazingBot(username, botToken);
        var bot = new HelloBot(config);

        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static Config config(String[] args) {
        try {
            return new Config(args[0], args[1], args[2].transform(Integer::parseInt));
        } catch (Exception e) {
            System.out.println("Cant start application with received args!");
            e.printStackTrace();
            throw new RuntimeException("Required args are {username, token, creatorId}");
        }
    }
}
