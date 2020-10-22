package com.belogrudov.javabot;

import com.belogrudov.javabot.botinstance.TelegramBotApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
@SpringBootApplication
public class JavabotApplication {

    public static void main(String[] args) {
        Logger.getGlobal().setLevel(Level.WARNING);
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new TelegramBotApplication());
        } catch (TelegramApiException ignored) {
        }
        SpringApplication.run(JavabotApplication.class, args);
    }

}
