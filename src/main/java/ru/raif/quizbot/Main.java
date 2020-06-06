package ru.raif.quizbot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Telegram API bot");
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        var username = args[0];
        var botToken = args[1];

        var bot = new MyAmazingBot(username, botToken);

        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
