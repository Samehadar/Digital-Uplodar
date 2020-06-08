package ru.raif.quizbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Answer(@JsonProperty("text") String text, @JsonProperty("correct") boolean correct) implements Serializable {}
