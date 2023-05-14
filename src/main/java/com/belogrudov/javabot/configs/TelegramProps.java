package com.belogrudov.javabot.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("telegram.bot")
@Component
@Data
public class TelegramProps {
    String name;
    String token;
}
