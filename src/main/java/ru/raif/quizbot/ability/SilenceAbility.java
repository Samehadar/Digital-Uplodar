package ru.raif.quizbot.ability;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class SilenceAbility implements AbilityExtension {

    @NonNull
    private final SilentSender silent;
    //trick to receive call-by-name behavior
    @NonNull
    private final Supplier<Integer> getCreatorId;

    public Reply ignoreAnyoneExceptCreator() {
        Consumer<Update> action = upd -> silent.send("The bot is under construction... Try again later.", AbilityUtils.getChatId(upd));

        return Reply.of(action, x -> !Objects.equals(x.getMessage().getFrom().getId(), getCreatorId.get()));
    }

}
