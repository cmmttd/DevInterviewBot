package com.belogrudov.javabot.configs;

import com.belogrudov.javabot.botinstance.TelegramBotApplication;
import com.belogrudov.javabot.controller.MessageDispatcher;
import com.belogrudov.javabot.data.QuestionsTable;
import com.belogrudov.javabot.data.UsersTable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class BotConfig {

    @Value("#{environment.BOT_TOKEN}")
    String botToken;

    @Value("#{environment.BOT_NAME}")
    String botName;

    @Autowired
    UsersTable usersTable;

    @Autowired
    QuestionsTable questionsTable;

    @Bean
    public TelegramBotApplication telegramBotApplication() {
        TelegramBotApplication telegramBotApplication = new TelegramBotApplication();
        telegramBotApplication.setBotName(botName);
        telegramBotApplication.setBotToken(botToken);
        return telegramBotApplication;
    }

    @Bean
    @Scope("prototype")
    public MessageDispatcher messageDispatcher(UsersTable usersTable) {
        return new MessageDispatcher(usersTable, questionsTable);
    }
}
