package com.grimeoverhere.uploaderbot.ability;

import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.CREATOR;

//trick to receive call-by-name behavior
public record BouncerAbility(DBContext db, SilentSender silent, Supplier<Integer> getCreatorId) implements AbilityExtension {

    private static final String WAIT_WHITE_LIST = "WAIT_WHITE_LIST";
    private static final String WHITE_LIST = "WHITE_LIST";

    public Reply rejectRequestsFromNonWhitelistUsers() {
        Consumer<Update> action = upd -> silent.send(
                "The bot is under construction... Try again later.",
                AbilityUtils.getChatId(upd)
        );

        return Reply.of(action, upd -> {
            if (db.getSet(WAIT_WHITE_LIST).contains(AbilityUtils.addTag(upd.getMessage().getFrom().getUserName()))) {
                db.getSet(WAIT_WHITE_LIST).remove(upd.getMessage().getFrom().getUserName());
                db.getSet(WHITE_LIST).add(upd.getMessage().getFrom().getId());
            }

            return !Objects.equals(upd.getMessage().getFrom().getId(), getCreatorId.get())
                    && !db.getSet(WHITE_LIST).contains(upd.getMessage().getFrom().getId());
        });
    }

    public Ability addToWhiteList() {
        return Ability.builder()
                .name("grant")
                .info("Add user to whitelist and allow him to use this bot")
                .locality(USER)
                .privacy(CREATOR)
                .input(1)
                .action(ctx -> {
                    db.<Integer, User>getMap("USERS")
                            .values().stream()
                            .filter(u -> u.getUserName() != null && AbilityUtils.addTag(u.getUserName()).equalsIgnoreCase(ctx.arguments()[0]))
                            .findAny()
                            .ifPresentOrElse(
                                    user -> {
                                        db.<String>getSet(WAIT_WHITE_LIST).remove(ctx.arguments()[0]);
                                        db.getSet(WHITE_LIST).add(user.getId());
                                        silent.send("User has been added in white list", AbilityUtils.getChatId(ctx.update()));
                                    },
                                    () -> {
                                        db.<String>getSet(WAIT_WHITE_LIST).add(ctx.arguments()[0]);
                                        silent.send("User not found, but has been added in the 'wait' white list", AbilityUtils.getChatId(ctx.update()));
                                    }
                            );
                }).build();
    }

    public Ability removeFromWhiteList() {
        return Ability.builder()
                .name("refuse")
                .info("Remove user from whitelist and deny him to use this bot")
                .locality(USER)
                .privacy(CREATOR)
                .input(1)
                .action(ctx -> {
                    db.<Integer, User>getMap("USERS")
                            .values().stream()
                            .filter(u -> u.getUserName() != null && AbilityUtils.addTag(u.getUserName()).equalsIgnoreCase(ctx.arguments()[0]))
                            .findAny()
                            .ifPresentOrElse(
                                    user -> {
                                        db.getSet(WHITE_LIST).remove(user.getId());
                                        db.<String>getSet(WAIT_WHITE_LIST).remove(ctx.arguments()[0]);
                                        silent.send("User has been removed from white list", AbilityUtils.getChatId(ctx.update()));
                                    },
                                    () -> {
                                        if (db.<String>getSet(WAIT_WHITE_LIST).contains(ctx.arguments()[0])) {
                                            db.<String>getSet(WAIT_WHITE_LIST).remove(ctx.arguments()[0]);
                                            silent.send("User has been removed from 'wait' white list", AbilityUtils.getChatId(ctx.update()));
                                        } else {
                                            silent.send("User not found", AbilityUtils.getChatId(ctx.update()));
                                        }
                                    }
                            );
                }).build();
    }

    public Ability showWhiteList() {
        return Ability.builder()
                .name("whitelist")
                .info("Remove user from whitelist and deny him to use this bot")
                .locality(USER)
                .privacy(CREATOR)
                .input(0)
                .action(ctx -> {
                    var whiteList = db.<Integer>getSet(WHITE_LIST)
                            .stream()
                            .map(id -> Optional.ofNullable(db.<Integer, User>getMap("USERS").get(id))
                                        .map(User::getUserName).map(AbilityUtils::addTag)
                                        .orElse("id:" + id)
                            )
                            .collect(Collectors.toList());
                    var waitWhiteList = db.<String>getSet(WAIT_WHITE_LIST);
                    silent.send(
                            whiteList.isEmpty() && waitWhiteList.isEmpty()
                            ? "Wait white list and White list is empty"
                            : whiteList.stream().collect(Collectors.joining("\n", "White list:\n", "\n")) + waitWhiteList.stream().collect(Collectors.joining("\n", "Wait white list:\n", "\n")),

                            AbilityUtils.getChatId(ctx.update())
                    );
                }).build();
    }

}
