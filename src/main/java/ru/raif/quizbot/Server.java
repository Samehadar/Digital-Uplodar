package ru.raif.quizbot;

import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.raif.quizbot.bot.QuizBot;
import ru.raif.quizbot.config.BotConfig;
import ru.raif.quizbot.config.ProxyConfig;
import ru.raif.quizbot.util.AsciiArt;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class Server {

    private final static Logger log = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        log.info(AsciiArt.drawLogotype());
        log.info("QuizBot is starting...");

        log.info("Loading server configuration");
        BotConfig config = ConfigBeanFactory.create(ConfigFactory.load("bot.conf").getConfig("bot"), BotConfig.class);
        log.debug(config.toString());
        log.info("Server configuration has been loaded successfully");
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        QuizBot bot;
        //ffs it doesnt work through Optional.map flow... I gave up and wrote through IF ELSE
        if (config.getProxy() != null) {
            DefaultBotOptions dbo = generateBotOptionsWithProxy(config.getProxy());
            bot = new QuizBot(config, dbo);
        } else {
            bot = new QuizBot(config);
        }


        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            log.error("Something went wrong during starting Telegram Bot...", e);
        }
    }

    private static DefaultBotOptions generateBotOptionsWithProxy(ProxyConfig config) {
        log.info(String.format("QuizBot is starting using proxy %s:%s", config.getHost(), config.getPort()));

        if (config.getUsername() != null && !config.getUsername().isBlank() && config.getPassword() != null) {
            log.info("Connection to proxy will be done using username and password");
            // Create the Authenticator that will return auth's parameters for proxy authentication
            Authenticator.setDefault(new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getUsername(), config.getPassword().toCharArray());
                }
            });
        }

        // Set up Http proxy
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        botOptions.setProxyHost(config.getHost());
        botOptions.setProxyPort(config.getPort());
        // Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

        return botOptions;
    }
}
