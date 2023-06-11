package com.belogrudov.javabot.botinstance;

import com.belogrudov.javabot.configs.TelegramProps;
import com.belogrudov.javabot.controller.MessageDispatcher;
import com.belogrudov.javabot.data.UsersRepo;
import com.belogrudov.javabot.utils.FilesUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotApplication extends TelegramLongPollingBot {

    //TODO: cleanup by rate
    //local users cache
    public static ConcurrentHashMap<Long, LocalDate> users = new ConcurrentHashMap<>();

    private final MessageDispatcher messageDispatcher;
    private final TelegramBotsApi session;
    private final TelegramProps props;
    private final FilesUtil filesUtil;
    private final UsersRepo usersRepo;

    @SneakyThrows
    @PostConstruct
    void init() {
        usersRepo.findAll().forEach(x -> TelegramBotApplication.users.put(x.getChatId(), LocalDate.now()));
        filesUtil.fillingDB();
        session.registerBot(this);
    }

    /**
     * Implements Telegram API method
     * Method intercepts updates with callback and normal message
     *
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
                SendMessage responseByCallback = messageDispatcher.getResponseByCallback(chatId, data);
                try {
                    execute(responseByCallback);
                } catch (TelegramApiException e) {
                    try {
                        responseByCallback.setParseMode("HTML");
                        responseByCallback.setText("<pre>" + responseByCallback.getText() + "</pre>");
                        execute(responseByCallback);
                    } catch (TelegramApiException ee) {
                        responseByCallback.enableMarkdown(false);
                        execute(responseByCallback);
                    }
                }
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
        return props.getName();
    }

    // TODO: 01/05/2023 Need to replace old-fashioned way
    @Override
    public String getBotToken() {
        return props.getToken();
    }
}
