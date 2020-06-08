package ru.raif.quizbot.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.raif.quizbot.config.BotConfig;
import ru.raif.quizbot.model.Quiz;
import ru.raif.quizbot.repository.QuizRepo;
import ru.raif.quizbot.util.MyTelegramLongPollingSessionBot;
import ru.raif.quizbot.util.QuizSender;

import java.util.Optional;

@Slf4j
public class ExampleBotWithSession extends MyTelegramLongPollingSessionBot {


    private BotConfig config;
    private QuizRepo quizRepo;

    public ExampleBotWithSession(BotConfig config, QuizRepo quizRepo) {
        this.config = config;
        this.quizRepo = quizRepo;
    }

    public ExampleBotWithSession(BotConfig config, QuizRepo quizRepo, DefaultBotOptions botOptions) {
        super(botOptions);
        this.config = config;
        this.quizRepo = quizRepo;
    }



    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update, Optional<Session> optionalSession) {
        //Do some action with update and session

        log.info("test");
        Quiz randomQuiz = quizRepo.getRandomQuiz();
        SendDice sendDice = new SendDice();

        sendDice.setReplyMarkup(new QuizSender(null).getQuizMarkup(randomQuiz));
        sendDice.setChatId(AbilityUtils.getChatId(update));
        sendApiMethod(sendDice);
    }

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

}