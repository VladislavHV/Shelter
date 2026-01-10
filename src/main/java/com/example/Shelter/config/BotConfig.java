package com.example.Shelter.config;

import com.example.Shelter.bot.ShelterBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfig {

    @Value("${telegram.bot.token:mock-token}")
    private String botToken;

    @Value("${telegram.bot.username:ShelterMockBot}")
    private String botUsername;

    @Bean
    @ConditionalOnProperty(name = "telegram.bot.mock", havingValue = "false", matchIfMissing = true)
    public ShelterBot shelterBot(
            @Value("${telegram.bot.token:#{null}}") String token,
            @Value("${telegram.bot.username:#{null}}") String username) {

        if (token == null || username == null) {
            System.out.println("Токен не указан, использую mock-режим");
            System.out.println("Для реального бота добавьте в application.properties:");
            System.out.println("telegram.bot.mock=false");
            System.out.println("telegram.bot.token=ВАШ_ТОКЕН");
            System.out.println("telegram.bot.username=ИМЯ_БОТА");

            // Создаем mock-бота
            return new ShelterBot("mock-token", "ShelterMockBot");
        }

        System.out.println("Создаю РЕАЛЬНОГО бота: " + username);
        return new ShelterBot(token, username);
    }

    @Bean
    @ConditionalOnProperty(name = "telegram.bot.mock", havingValue = "false", matchIfMissing = true)
    public BotSession botSession(ShelterBot shelterBot) {
        System.out.println("Запускаю сессию для бота: " + shelterBot.getBotUsername());

        // Если это mock-бот (токен содержит "mock"), не создаем реальную сессию
        if (shelterBot.getBotToken().contains("mock")) {
            System.out.println("Mock-бот готов к работе (без Telegram API)");
            return createMockSession();
        }

        DefaultBotSession session = new DefaultBotSession();
        session.setToken(shelterBot.getBotToken());
        session.setCallback(shelterBot);

        DefaultBotOptions options = new DefaultBotOptions();
        options.setGetUpdatesLimit(100);
        options.setGetUpdatesTimeout(50);
        session.setOptions(options);

        session.start();
        checkBotAvailability(shelterBot);

        return session;
    }

    private BotSession createMockSession() {
        return new BotSession() {
            @Override public void start() { System.out.println("MOCK Сессия запущена"); }
            @Override public void stop() { System.out.println("MOCK Сессия остановлена"); }
            @Override public boolean isRunning() { return true; }
            @Override public void setToken(String token) { }
            @Override public void setCallback(LongPollingBot callback) { }
            @Override public void setOptions(BotOptions options) { }
        };
    }

    private void checkBotAvailability(ShelterBot bot) {
        try {
            bot.execute(new GetMe());
            System.out.println("Бот приюта '" + bot.getBotUsername() + "'успешно запущен");
        } catch (TelegramApiException e) {
            System.out.println("️Предупреждение: " + e.getMessage());
        }
    }

}
