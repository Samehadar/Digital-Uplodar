package ru.raif.quizbot.repository;

import ru.raif.quizbot.model.Answer;
import ru.raif.quizbot.model.Quiz;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static ru.raif.quizbot.model.Quiz.QuizLevel.*;

public class QuizRepoImpl implements QuizRepo {

    List<Quiz> quizzes;

    public QuizRepoImpl() {
        quizzes = List.of(
                new Quiz(1L, EASY, "Is A?", List.of(
                        new Answer("A", true),
                        new Answer("B", false),
                        new Answer("C", false),
                        new Answer("D", false)
                )),
                new Quiz(2L, EASY, "Is D?", List.of(
                        new Answer("A", false),
                        new Answer("B", false),
                        new Answer("C", false),
                        new Answer("D", true)
                )),
                new Quiz(3L, EASY, "Is C?", List.of(
                        new Answer("A", false),
                        new Answer("B", false),
                        new Answer("C", true),
                        new Answer("D", false)
                )),
                new Quiz(4L, MEDIUM, "Is AA?", List.of(
                        new Answer("AA", true),
                        new Answer("BB", false),
                        new Answer("CC", false),
                        new Answer("DD", false)
                )),
                new Quiz(5L, MEDIUM, "Is BB?", List.of(
                        new Answer("AA", false),
                        new Answer("BB", true),
                        new Answer("CC", false),
                        new Answer("DD", false)
                )),
                new Quiz(6L, MEDIUM, "Is CC?", List.of(
                        new Answer("AA", false),
                        new Answer("BB", false),
                        new Answer("CC", true),
                        new Answer("DD", false)
                )),
                new Quiz(7L, MEDIUM, "Is DD?", List.of(
                        new Answer("AA", false),
                        new Answer("BB", false),
                        new Answer("CC", false),
                        new Answer("DD", true)
                )),
                new Quiz(8L, HARD, "Is BBB?", List.of(
                        new Answer("AAA", false),
                        new Answer("BBB", true),
                        new Answer("CCC", false),
                        new Answer("DDD", false)
                )),
                new Quiz(9L, HARD, "Is CCC?", List.of(
                        new Answer("AAA", false),
                        new Answer("BBB", false),
                        new Answer("CCC", true),
                        new Answer("DDD", false)
                )),
                new Quiz(10L, HARD, "Is DDD?", List.of(
                        new Answer("AAA", false),
                        new Answer("BBB", false),
                        new Answer("CCC", false),
                        new Answer("DDD", true)
                ))
        );
    }

    @Override
    public Quiz getRandomQuiz() {
        int randomElementIndex
                = ThreadLocalRandom.current().nextInt(quizzes.size());
        return quizzes.get(randomElementIndex);
    }

    @Override
    public Quiz getRandomQuiz(Quiz.QuizLevel level) {
        List<Quiz> filtered = quizzes.stream()
                .filter(quiz -> level.equals(quiz.level()))
                .collect(Collectors.toList());
        int randomElementIndex = ThreadLocalRandom.current().nextInt(filtered.size());

        return filtered.get(randomElementIndex);
    }

    @Override
    public List<Quiz> take(Integer countAnyLevel) {
        var givenList = new ArrayList<>(quizzes);
        Collections.shuffle(givenList);

        return givenList.subList(0, countAnyLevel);
    }

    @Override
    public List<Quiz> take(Integer count, Quiz.QuizLevel level) {
        var givenList = new ArrayList<>(quizzes);
        Collections.shuffle(givenList);

        return givenList.stream()
                .filter(quiz -> level.equals(quiz.level()))
                .limit(count)
                .collect(Collectors.toList());
    }

}
