package com.belogrudov.javabot.botinstance;

import com.belogrudov.javabot.controller.MessageDispatcher;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramBotApplication extends TelegramLongPollingBot {
    @Setter
    String botToken;
    @Setter
    String botName;

    //TODO: cleanup by rate
    //local users cache
    static ConcurrentHashMap<Long, LocalDate> users = new ConcurrentHashMap<>();

    @Autowired
    MessageDispatcher messageDispatcher;

    /**
     * Implements Telegram API method
     * Method intercepts updates with callback and normal message
     * @param update
     */
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        log.info(update.toString() + "\n---");

        Message message = null;
        String data = null;
        boolean hasCallback = false,
                hasRegularMessage = false;

        //take apart incoming update
        if (update.hasCallbackQuery()) {
            hasCallback = true;
            CallbackQuery callbackQuery = update.getCallbackQuery();
            message = callbackQuery.getMessage();
            data = callbackQuery.getData();
        } else if (update.hasMessage()) {
            hasRegularMessage = true;
            message = update.getMessage();
            data = message.getText();
        }

        if (hasCallback || hasRegularMessage) {
            Long chatId = message.getChatId();
            String userName = Optional.ofNullable(message.getFrom().getUserName())
                    .orElse(message.getFrom().getFirstName() + " " + message.getFrom().getLastName());

            //routing by request
            if (hasCallback) {
                execute(messageDispatcher.getResponseByCallback(chatId, data));
            } else if (!users.containsKey(chatId)) {
                users.put(chatId, LocalDate.now());
                messageDispatcher.registerIfAbsent(chatId, userName);
                execute(messageDispatcher.getWelcomeMessage(chatId, userName));
            } else {
                execute(messageDispatcher.getResponseByRegularMessage(chatId, data));
            }
        }

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
