package ru.raif.quizbot.ability;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.abilitybots.api.util.AbilityUtils;
import ru.raif.quizbot.repository.QuizRepo;
import ru.raif.quizbot.util.QuizSender;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

//не работает потому что библиотека сначала загружает все абилити, а потом продолжает инициализацию конструктора
// таким образом у меня все параметры моего аьбилити, которых не было в конструкторе предка - нулы
//@Slf4j - it doesnt work with records ((
public record SendQuizAbility(QuizSender sender, QuizRepo quizRepo) implements AbilityExtension {

    private static final Logger log = LoggerFactory.getLogger(SendQuizAbility.class);

    public Ability sendQuiz() {
        return Ability.builder()
                .name("quiz")
                .info("Return random quiz")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> sender.sendQuiz(quizRepo.getRandomQuiz(), ctx.chatId()))
                .reply(update -> {
                    log.info("Received reply from the user: " + AbilityUtils.fullName(AbilityUtils.getUser(update)));
                    update.getCallbackQuery();
                    sender.send("I don't know the correct answer, I'm sorry :(", AbilityUtils.getChatId(update));
                })
                .build();
    }
}
