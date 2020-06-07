package ru.raif.quizbot.ability;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.objects.ReplyFlow;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Predicate;

@RequiredArgsConstructor
public class LeftRightAbility implements AbilityExtension {

    @NonNull
    private final DBContext db;
    @NonNull
    private final SilentSender silent;



    public Reply goLeftGoRight() {
        Reply saidLeft = Reply.of(upd -> silent.send("Sir, I have gone left.", AbilityUtils.getChatId(upd)),
                messageIs("go left or else"));

        ReplyFlow leftflow = ReplyFlow.builder(db)
                .action(upd -> silent.send("I don't know how to go left.", AbilityUtils.getChatId(upd)))
                .onlyIf(messageIs("left"))
                .next(saidLeft)
                .build();

        Reply saidRight = Reply.of(upd -> silent.send("Sir, I have gone right.", AbilityUtils.getChatId(upd)),
                messageIs("right"));

        ReplyFlow wake_up = ReplyFlow.builder(db)
                // Just like replies, a ReplyFlow can take an action, here we want to send a
                // statement to prompt the user for directions!
                .action(upd -> silent.send("Command me to go left or right!", AbilityUtils.getChatId(upd)))
                // We should only trigger this flow when the user says "wake up"
                .onlyIf(messageIs("wake up"))
                // The next method takes in an object of type Reply.
                // Here we chain our replies together
                .next(leftflow)
                // We chain one more reply, which is when the user commands your bot to go right
                .next(saidRight)
                // Finally, we build our ReplyFlow
                .build();

        return wake_up;
    }

    private Predicate<Update> messageIs(String msg) {
        return upd -> upd.getMessage().getText().equalsIgnoreCase(msg);
    }

}
