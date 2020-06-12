package com.grimeoverhere.uploaderbot.ability;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.abilitybots.api.util.AbilityExtension;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@RequiredArgsConstructor
public class PingPongAbility implements AbilityExtension {

    @NonNull
    private final SilentSender silent;

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
}
