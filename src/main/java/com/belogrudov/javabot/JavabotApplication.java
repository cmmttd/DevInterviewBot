package com.belogrudov.javabot;

import com.belogrudov.javabot.botinstance.TelegramBotApplication;
import com.belogrudov.javabot.controller.MessageDispatcher;
import com.belogrudov.javabot.data.QuestionsTable;
import com.belogrudov.javabot.utils.FilesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
@SpringBootApplication
public class JavabotApplication {

    @Autowired
    FilesUtil filesUtil;

    @Autowired
    QuestionsTable questionsTable;

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

    @PostConstruct
    public void init() {
//        filesUtil.fillingDB();
        MessageDispatcher.maxQNumber = questionsTable.findAll().size();
    }
}
