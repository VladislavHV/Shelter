package com.example.Shelter.bot;

import com.example.Shelter.bot.handlers.MessageHandler;
import com.example.Shelter.bot.handlers.CallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ShelterBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private CallbackHandler callbackHandler;

    public ShelterBot(
            @Value("${telegram.bot.token:#{null}}") String botToken,
            @Value("${telegram.bot.username:#{null}}") String botUsername) {

        super(resolveToken(botToken));  // Выносим логику в отдельный метод

        this.botToken = resolveToken(botToken);
        this.botUsername = resolveUsername(botUsername);

        System.out.println("Инициализация бота приюта:");
        System.out.println("Режим: " + (botToken == null ? "MOCK" : "REAL"));
        System.out.println("Имя: " + this.botUsername);
    }

    private static String resolveToken(String token) {
        return (token == null || token.isEmpty() || token.contains("placeholder"))
                ? "mock-token-" + System.currentTimeMillis()
                : token;
    }

    private static String resolveUsername(String username) {
        return (username == null || username.isEmpty())
                ? "ShelterMockBot"
                : username;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                messageHandler.handleMessage(update.getMessage(), this);
            } else if (update.hasCallbackQuery()) {
                callbackHandler.handleCallbackQuery(update.getCallbackQuery(), this);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
