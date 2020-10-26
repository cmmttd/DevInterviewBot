package com.belogrudov.javabot.botinstance;

import com.belogrudov.javabot.controller.MessageDispatcher;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramBotApplication extends TelegramLongPollingBot {
    String botToken;
    String botName;

    @Autowired
    MessageDispatcher messageDispatcher;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        log.debug(update.toString());
        Message message = update.getMessage();
        Long chatId = message.getChatId();

        if (update.hasMessage() && message.hasText()) {
            execute(messageDispatcher.getResponse(message));
        } else {
            execute(onInvalidRequest(chatId));
        }
    }

    private SendMessage onInvalidRequest(long chatId) {
        return new SendMessage().setChatId(chatId).setText("Not valid request");
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
