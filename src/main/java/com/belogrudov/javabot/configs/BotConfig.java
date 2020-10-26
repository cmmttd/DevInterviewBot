package com.belogrudov.javabot.configs;

import com.belogrudov.javabot.botinstance.TelegramBotApplication;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class BotConfig {

    @Value("#{environment.BOT_TOKEN}")
    String botToken;

    @Value("#{environment.BOT_NAME}")
    String botName;

    @Bean
    public TelegramBotApplication telegramBotApplication() {
        TelegramBotApplication bot = new TelegramBotApplication();
        bot.setBotName(botName);
        bot.setBotToken(botToken);
        return bot;
    }
}
