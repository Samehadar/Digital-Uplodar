package ru.raif.quizbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {

    private final static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting Telegram API bot");
        Config config = config(args);

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

//        var bot = new MyAmazingBot(username, botToken);
        var bot = new HelloBot(config);

        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            log.error("Something went wrong during starting Telegram Bot...", e);
        }
    }

    private static Config config(String[] args) {
        try {
            return new Config(args[0], args[1], args[2].transform(Integer::parseInt));
        } catch (Exception e) {
            log.error("Cant start application with received args!", e);
            throw new RuntimeException("Required args are {username, token, creatorId}", e);
        }
    }
}
