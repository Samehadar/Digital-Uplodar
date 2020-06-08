package ru.raif.quizbot.ability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.raif.quizbot.model.Answer;
import ru.raif.quizbot.model.Quiz;
import ru.raif.quizbot.repository.QuizRepo;
import ru.raif.quizbot.util.QuizSender;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.telegram.abilitybots.api.objects.Flag.MESSAGE;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

//не работает потому что библиотека сначала загружает все абилити, а потом продолжает инициализацию конструктора
// таким образом у меня все параметры моего аьбилити, которых не было в конструкторе предка - нулы
//@Slf4j - it doesnt work with records ((
public record SendQuizAbility(Supplier<QuizSender> quizSender, Supplier<QuizRepo> quizRepo, DBContext db) implements AbilityExtension {

    private static final String QUIZZES = "QUIZZES";
    private static final Logger log = LoggerFactory.getLogger(SendQuizAbility.class);

    public Ability sendQuiz() {
        return Ability.builder()
                .name("quiz")
                .info("Return random quiz")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    Map<String, Quiz> quizMap = db.getMap(QUIZZES);
                    Quiz randomQuiz = quizRepo.get().getRandomQuiz();
                    quizMap.put(ctx.chatId().toString(), randomQuiz);
                    quizSender.get().sendQuiz(randomQuiz, ctx.chatId());
                })
                .reply(replyOnQuiz())
                .build();
    }

    private Reply replyOnQuiz() {
        return Reply.of(update -> {
            Map<String, Quiz> quizMap = db.getMap(QUIZZES);
            log.info("Received reply from the user: " + AbilityUtils.fullName(AbilityUtils.getUser(update)));
            Quiz quiz = quizMap.get(AbilityUtils.getChatId(update).toString());
            quizMap.remove(AbilityUtils.getChatId(update).toString());
            boolean isRight = quiz.answers().stream().anyMatch(answer -> answer.text().equals(update.getMessage().getText()) && answer.correct());
            if (isRight) {
                quizSender.get().sendAnswer("You're right!", AbilityUtils.getChatId(update));
            } else {
                quizSender.get().sendAnswer("You're wrong.", AbilityUtils.getChatId(update));
            }
        }, MESSAGE, isAnswerOnQuiz());
    }

    private Predicate<Update> isAnswerOnQuiz() {
        return upd -> {
            if (upd.hasMessage() && upd.getMessage().hasText()) {
                Quiz quiz = db.<String, Quiz>getMap(QUIZZES).get(AbilityUtils.getChatId(upd).toString());
                if (quiz != null) {
                    String answer = upd.getMessage().getText();
                    return quiz.answers().stream().map(Answer::text).anyMatch(answer::equalsIgnoreCase);
                } else return false;
            } else return false;
        };
    }
}
