package com.grimeoverhere.uploaderbot.util;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.session.ChatIdConverter;
import org.telegram.telegrambots.session.DefaultChatIdConverter;
import org.telegram.telegrambots.session.DefaultChatSessionContext;

import java.util.Optional;

@SuppressWarnings({"WeakerAccess", "OptionalUsedAsFieldOrParameterType", "unused"})
public abstract class MyTelegramLongPollingSessionBot extends TelegramLongPollingBot {
    DefaultSessionManager sessionManager;

    ChatIdConverter chatIdConverter;

    public MyTelegramLongPollingSessionBot(){
        this(new DefaultChatIdConverter());
    }

    public MyTelegramLongPollingSessionBot(DefaultBotOptions options){
        this(new DefaultChatIdConverter(), options);
    }

    public MyTelegramLongPollingSessionBot(ChatIdConverter chatIdConverter){
        this(chatIdConverter, ApiContext.getInstance(DefaultBotOptions.class));
    }

    public MyTelegramLongPollingSessionBot(ChatIdConverter chatIdConverter, DefaultBotOptions options){
        super(options);
        this.setSessionManager(new DefaultSessionManager());
        this.setChatIdConverter(chatIdConverter);
        AbstractSessionDAO sessionDAO = (AbstractSessionDAO) sessionManager.getSessionDAO();
        sessionDAO.setSessionIdGenerator(chatIdConverter);
    }

    public void setSessionManager(DefaultSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setChatIdConverter(ChatIdConverter chatIdConverter) {
        this.chatIdConverter = chatIdConverter;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Optional<Session> chatSession;
        Message message;
        if (update.hasMessage()) {
            message = update.getMessage();
        } else if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
        } else {
            chatSession = Optional.empty();
            onUpdateReceived(update, chatSession);
            return;
        }
        chatIdConverter.setSessionId(message.getChatId());
        chatSession = this.getSession(message);
        onUpdateReceived(update, chatSession);
    }

    public Optional<Session> getSession(Message message){
        try {
            return Optional.of(sessionManager.getSession(chatIdConverter));
        } catch (UnknownSessionException e) {
            SessionContext botSession = new DefaultChatSessionContext(message.getChatId(), message.getFrom().getUserName());
            return Optional.of(sessionManager.start(botSession));
        }
    }

    public abstract void onUpdateReceived(Update update, Optional<Session> botSession);
}
