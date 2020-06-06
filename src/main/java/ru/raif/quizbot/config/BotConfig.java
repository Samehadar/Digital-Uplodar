package ru.raif.quizbot.config;

import com.typesafe.config.Optional;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data @RequiredArgsConstructor @NoArgsConstructor
public class BotConfig implements Serializable {
    @NonNull
    String username;
    @NonNull
    String token;
    @NonNull
    Integer creatorId;
    @Optional
    ProxyConfig proxy;
}