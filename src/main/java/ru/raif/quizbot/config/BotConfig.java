package ru.raif.quizbot.config;

import com.typesafe.config.Optional;

import java.io.Serializable;

public class BotConfig implements Serializable {
    String username;
    String token;
    Integer creatorId;
    @Optional
    ProxyConfig proxy;

    public BotConfig() {}

    public BotConfig(String username, String token, Integer creatorId, ProxyConfig proxy) {
        this.username = username;
        this.token = token;
        this.creatorId = creatorId;
        this.proxy = proxy;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public ProxyConfig getProxy() {
        return proxy;
    }

    public void setProxy(ProxyConfig proxy) {
        this.proxy = proxy;
    }

}