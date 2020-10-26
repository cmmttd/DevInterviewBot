package com.belogrudov.javabot.controller;

import com.belogrudov.javabot.data.RepoInterface;
import com.belogrudov.javabot.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class MessageDispatcher {

    @Autowired
    RepoInterface repoInterface;

    public SendMessage getResponse(Message message) {
        repoInterface.saveAndFlush(new User(123123L, "First"));
        String s = repoInterface.findAll().stream().map(User::toString).collect(Collectors.joining("\n"));
        return new SendMessage().setChatId(message.getChatId()).setText("Hello! \n" + s);
    }
}
