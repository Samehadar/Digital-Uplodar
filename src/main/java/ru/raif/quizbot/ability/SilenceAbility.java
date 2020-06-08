package ru.raif.quizbot.ability;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class SilenceAbility implements AbilityExtension {

    List<Integer> whitelist = List.of(258311819, 331620369, 180329211, 258311819);

    @NonNull
    private final SilentSender silent;
    //trick to receive call-by-name behavior
    @NonNull
    private final Supplier<Integer> getCreatorId;

    public Reply rejectRequestsFromNonWhitelistUsers() {
        Consumer<Update> action = upd -> silent.send(
                "The bot is under construction... Try again later.",
                AbilityUtils.getChatId(upd)
        );

        return Reply.of(action, x -> !Objects.equals(x.getMessage().getFrom().getId(), getCreatorId.get())
                && !whitelist.contains(x.getMessage().getFrom().getId()));
    }

}
