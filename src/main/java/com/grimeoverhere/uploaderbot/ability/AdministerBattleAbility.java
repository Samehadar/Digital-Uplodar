package com.grimeoverhere.uploaderbot.ability;

import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.abilitybots.api.util.AbilityUtils;

import java.util.UUID;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.ADMIN;

public record AdministerBattleAbility(SilentSender silent) implements AbilityExtension {


    public Ability registerParticipant() {
        return Ability.builder()
                .name("register")
                .info("Register new participant of GOH & GMG producers battle")
                .locality(ALL)
                .privacy(ADMIN)
                .input(1)
                .action(ctx -> {
                    silent.send(String.format("""
                            MC's has been registered in the battle. 
                            This is his UUID for uploading songs: 
                            %s
                            """, UUID.randomUUID().toString()),
                            AbilityUtils.getChatId(ctx.update())
                    );
                })
                .build();
    }

    public Ability participants() {
        return Ability.builder()
                .name("participants")
                .info("Return participants of GOH & GMG producers battle")
                .locality(ALL)
                .privacy(ADMIN)
                .action(ctx -> {
                    silent.send(
                            "Participants: empty",
                            AbilityUtils.getChatId(ctx.update())
                    );
                })
                .build();
    }



}
