package com.grimeoverhere.uploaderbot.bot;

import com.grimeoverhere.uploaderbot.ability.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.grimeoverhere.uploaderbot.config.BotConfig;
import com.grimeoverhere.uploaderbot.repository.QuizRepo;
import com.grimeoverhere.uploaderbot.util.QuizSender;

import java.util.function.Predicate;

import static org.telegram.abilitybots.api.objects.Flag.MESSAGE;
import static org.telegram.abilitybots.api.objects.Flag.REPLY;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

//todo:: добавить рандомное предложение от бота ответить еще раз, типа "Ой, что-то пошло не так, ответь еще раз (псс, овтет не верный, давай еще раз потихому)
@Slf4j
public class UploaderBot extends AbilityBot {

    private Integer creatorId;
    @Getter
    private QuizSender quizSender;
    @Getter
    private QuizRepo quizRepo;

    public UploaderBot(BotConfig config, QuizRepo quizRepo) {
        super(config.getToken(), config.getUsername());
        this.creatorId = config.getCreatorId();
        this.quizSender = new QuizSender(sender);
        this.quizRepo = quizRepo;
    }

    public UploaderBot(BotConfig config, QuizRepo quizRepo, DefaultBotOptions botOptions) {
        super(config.getToken(), config.getUsername(), botOptions);
        this.creatorId = config.getCreatorId();
        this.quizSender = new QuizSender(sender);
        this.quizRepo = quizRepo;
    }

    @Override
    public int creatorId() {
        return creatorId;
    }

    public AbilityExtension registration() {
        return new AdministerBattleAbility(silent);
    }

    public AbilityExtension ignoreAnyoneExceptCreator() {
        return new BouncerAbility(db, silent, this::creatorId);
    }

    public AbilityExtension pingPong() {
        return new PingPongAbility(silent);
    }

    public AbilityExtension goLeftGoRight() {
        return new LeftRightAbility(db, silent);
    }

    public Ability playWithMe() {
        String playMessage = "Play with me!";

        return Ability.builder()
                .name("play")
                .info("Do you want to play with me?")
                .privacy(PUBLIC)
                .locality(ALL)
                .input(0)
                .action(ctx -> silent.forceReply(playMessage, ctx.chatId()))
                // The signature of a reply is -> (Consumer<Update> action, Predicate<Update>... conditions)
                // So, we  first declare the action that takes an update (NOT A MESSAGECONTEXT) like the action above
                // The reason of that is that a reply can be so versatile depending on the message, context becomes an inefficient wrapping
                .reply(upd -> {
                            // Prints to console
                            log.info("I'm in a reply!");
                            if (upd.getMessage().getFrom().getId().equals(331620369)) {
                                silent.send("Baka!! Don't text me so late!", upd.getMessage().getChatId());
                            } else
                                // Sends message
                                silent.send("It's been nice playing with you!", upd.getMessage().getChatId());
                        },
                        // Now we start declaring conditions, MESSAGE is a member of the enum Flag class
                        // That class contains out-of-the-box predicates for your replies!
                        // MESSAGE means that the update must have a message
                        // This is imported statically, Flag.MESSAGE
                        MESSAGE,
                        // REPLY means that the update must be a reply, Flag.REPLY
                        REPLY,
                        // A new predicate user-defined
                        // The reply must be to the bot
                        isReplyToBot(),
                        // If we process similar logic in other abilities, then we have to make this reply specific to this message
                        // The reply is to the playMessage
                        isReplyToMessage(playMessage)
                )
                // You can add more replies by calling .reply(...)
                .build();
    }

    private Predicate<Update> isReplyToMessage(String message) {
        return upd -> {
            Message reply = upd.getMessage().getReplyToMessage();
            return reply.hasText() && reply.getText().equalsIgnoreCase(message);
        };
    }

    private Predicate<Update> isReplyToBot() {
        return upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername());
    }

}
