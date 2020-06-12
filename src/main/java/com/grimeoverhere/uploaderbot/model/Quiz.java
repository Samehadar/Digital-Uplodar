package com.grimeoverhere.uploaderbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Quiz(@JsonProperty("id") Long id,
                   @JsonProperty("level") QuizLevel level,
                   @JsonProperty("question") String question,
                   @JsonProperty("answers") List<Answer> answers) implements Serializable {

    public enum QuizLevel {
        EASY, MEDIUM, HARD
    }

}
