package ru.raif.quizbot.model;

import java.util.List;

public record Quiz(Long id, QuizLevel level, String question, List<Answer>answers) {

    public enum QuizLevel {
        EASY, MEDIUM, HARD
    }

}
