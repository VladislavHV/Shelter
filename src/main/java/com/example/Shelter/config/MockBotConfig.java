package com.example.Shelter.config;

import com.example.Shelter.bot.ShelterBot;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Scanner;

@Configuration
public class MockBotConfig {

    @Bean
    @ConditionalOnProperty(name = "telegram.bot.mock", havingValue = "true")
    public ShelterBot mockShelterBot() {
        return new ShelterBot("mock-token", "ShelterMockBot") {
            private final Scanner scanner = new Scanner(System.in);

            @Override
            public void onUpdateReceived(Update update) {
                if (update.hasMessage() && update.getMessage().hasText()) {
                    String text = update.getMessage().getText();
                    String chatId = update.getMessage().getChatId().toString();

                    System.out.println("MOCK БОТ Получено сообщение:");
                    System.out.println("Чат ID: " + chatId);
                    System.out.println("Текст: " + text);
                    System.out.println("Время: " + update.getMessage().getDate());

                    // Имитируем обработку команд приюта
                    String response = processShelterCommand(text);

                    System.out.println("Ответ: " + response);
                    System.out.print("\nВведите следующее сообщение (или 'exit' для выхода): ");
                }
            }

            private String processShelterCommand(String text) {
                switch (text.toLowerCase()) {
                    case "/start":
                        return "Добро пожаловать в приют животных! Выберите команду:\n" +
                                "/info - Информация о приюте\n" +
                                "/adopt - Как взять животное\n" +
                                "/help - Помощь";
                    case "/info":
                        return "Наш приют работает с 2010 года. У нас 50 животных ждут дома!";
                    case "/adopt":
                        return "Чтобы взять животное:\n1. Посетите приют\n2. Заполните анкету\n3. Пройдите собеседование";
                    case "/help":
                        return "Свяжитесь с волонтером: +7-XXX-XXX-XX-XX";
                    default:
                        return "Я вас не понял. Используйте /start для списка команд";
                }
            }

            @PostConstruct
            public void startMockChat() {
                System.out.println("\n" + "═".repeat(50));
                System.out.println("ЗАПУЩЕН MOCK РЕЖИМ БОТА ДЛЯ ПРИЮТА");
                System.out.println("═".repeat(50));
                System.out.println("\nЭмуляция Telegram чата. Команды:");
                System.out.println("/start - Начать диалог");
                System.out.println("/info  - Информация о приюте");
                System.out.println("/adopt - Взять животное");
                System.out.println("/help  - Помощь");
                System.out.println("exit   - Выйти");
                System.out.println("\n" + "─".repeat(50));

                new Thread(() -> {
                    while (true) {
                        System.out.print("\nВведите команду для бота: ");
                        String input = scanner.nextLine();

                        if ("exit".equalsIgnoreCase(input)) {
                            System.out.println("Завершение mock-режима...");
                            System.exit(0);
                        }

                        //Создаем mock Update
                        Update update = new Update();
                        Message message = new Message();
                        message.setText(input);
                        message.setChat(new Chat(123456789L, "private"));
                        message.setDate((int) (System.currentTimeMillis() / 1000));
                        update.setMessage(message);

                        //Отправляем в бота
                        this.onUpdateReceived(update);
                    }
                }).start();
            }
        };
    }

    @Bean
    @ConditionalOnProperty(name = "telegram.bot.mock.enabled", havingValue = "true")
    public BotSession mockBotSession() {
        System.out.println("Mock-бот для приюта готов к работе!");

        return new BotSession() {
            private boolean running = true;

            @Override
            public void start() {
                System.out.println("MOCK Сессия бота 'запущена'");
                running = true;
            }

            @Override
            public void stop() {
                System.out.println("MOCK Сессия бота 'остановлена'");
                running = false;
            }

            @Override
            public boolean isRunning() {
                return running;
            }

            //Дополнительные методы интерфейса BotSession
            @Override
            public void setToken(String token) {
                System.out.println("MOCK Токен установлен: " +
                        (token != null ? token.substring(0, Math.min(8, token.length())) + "..." : "null"));
            }

            @Override
            public void setCallback(LongPollingBot callback) {
                System.out.println("MOCK Callback установлен: " + callback.getBotUsername());
            }

            @Override
            public void setOptions(BotOptions options) {
                System.out.println("MOCK Опции установлены");
            }

            public void setUpdatesSupplier(DefaultBotSession.UpdatesSupplier supplier) {
                System.out.println("MOCK UpdatesSupplier установлен");
            }
        };
    }
}
