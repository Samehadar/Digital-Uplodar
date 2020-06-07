package ru.raif.quizbot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.objects.ReplyFlow;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.raif.quizbot.config.BotConfig;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.telegram.abilitybots.api.objects.Flag.MESSAGE;
import static org.telegram.abilitybots.api.objects.Flag.REPLY;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

public class QuizBot extends AbilityBot {

    private final static Logger log = LoggerFactory.getLogger(QuizBot.class);

    private Integer creatorId;

    public QuizBot(BotConfig config) {
        super(config.getToken(), config.getUsername());
        this.creatorId = config.getCreatorId();
    }

    public QuizBot(BotConfig config, DefaultBotOptions botOptions) {
        super(config.getToken(), config.getUsername(), botOptions);
        this.creatorId = config.getCreatorId();
    }

    @Override
    public int creatorId() {
        return creatorId;
    }

    public Reply ignoreAnyoneExceptCreator() {
        Consumer<Update> action = upd -> silent.send("The bot is under construction... Try again later.", AbilityUtils.getChatId(upd));

        return Reply.of(action, x -> !Objects.equals(x.getMessage().getFrom().getId(), creatorId));
    }

    public Ability pingPong() {
        return Ability
                .builder()
                .name("ping")
                .info("ping pong")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.send("pong", ctx.chatId()))
                .build();
    }

    public Ability sayHelloWorld() {
        return Ability
                .builder()
                .name("hello")
                .info("says hello world!")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.send("Hello world!", ctx.chatId()))
                .build();
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

    private Predicate<Update> hasMessageWith(String msg) {
        return upd -> upd.getMessage().getText().equalsIgnoreCase(msg);
    }

    public Reply moves() {
        Reply saidLeft = Reply.of(upd -> silent.send("Sir, I have gone left.", AbilityUtils.getChatId(upd)),
                hasMessageWith("go left or else"));

        ReplyFlow leftflow = ReplyFlow.builder(db)
                .action(upd -> silent.send("I don't know how to go left.", AbilityUtils.getChatId(upd)))
                .onlyIf(hasMessageWith("left"))
                .next(saidLeft)
                .build();

        Reply saidRight = Reply.of(upd -> silent.send("Sir, I have gone right.", AbilityUtils.getChatId(upd)),
                hasMessageWith("right"));

        ReplyFlow wake_up = ReplyFlow.builder(db)
                // Just like replies, a ReplyFlow can take an action, here we want to send a
                // statement to prompt the user for directions!
                .action(upd -> silent.send("Command me to go left or right!", AbilityUtils.getChatId(upd)))
                // We should only trigger this flow when the user says "wake up"
                .onlyIf(hasMessageWith("wake up"))
                // The next method takes in an object of type Reply.
                // Here we chain our replies together
                .next(leftflow)
                // We chain one more reply, which is when the user commands your bot to go right
                .next(saidRight)
                // Finally, we build our ReplyFlow
                .build();

        return wake_up;
    }

}
