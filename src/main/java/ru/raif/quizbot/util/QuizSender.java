package ru.raif.quizbot.util;

import io.vavr.collection.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;
import ru.raif.quizbot.model.Quiz;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public record QuizSender(MessageSender sender) {

    private static final Logger log = LoggerFactory.getLogger(QuizSender.class);

    public Optional<Message> sendQuiz(Quiz quiz, long id) {
        KeyboardRow keyboardButtons = new KeyboardRow();
        Stream.ofAll(quiz.answers())
                .zipWithIndex()
                .forEach(answI -> keyboardButtons.add(answI._2(), answI._1().text()));
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(List.of(keyboardButtons));

        SendMessage msg = new SendMessage();
        msg.setChatId(id);
        msg.setText(quiz.question());
        msg.enableMarkdown(true);
        msg.setReplyMarkup(keyboard);

        return execute(msg);
    }


    public Optional<Message> send(String message, long id) {
        return doSendMessage(message, id, false);
    }

    public Optional<Message> sendMd(String message, long id) {
        return doSendMessage(message, id, true);
    }

    public Optional<Message> forceReply(String message, long id) {
        SendMessage msg = new SendMessage();
        msg.setText(message);
        msg.setChatId(id);
        msg.setReplyMarkup(new ForceReplyKeyboard());

        return execute(msg);
    }

    public <T extends Serializable, Method extends BotApiMethod<T>> Optional<T> execute(Method method) {
        try {
            return Optional.ofNullable(sender.execute(method));
        } catch (TelegramApiException e) {
            log.error("Could not execute bot API method", e);
            return Optional.empty();
        }
    }

    public <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>> void
    executeAsync(Method method, Callback callable) {
        try {
            sender.executeAsync(method, callable);
        } catch (TelegramApiException e) {
            log.error("Could not execute bot API method", e);
        }
    }

    private Optional<Message> doSendMessage(String txt, long groupId, boolean format) {
        SendMessage smsg = new SendMessage();
        smsg.setChatId(groupId);
        smsg.setText(txt);
        smsg.enableMarkdown(format);

        return execute(smsg);
    }
}
