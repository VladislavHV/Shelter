package com.example.Shelter.bot;

import com.example.Shelter.bot.handlers.MessageHandler;
import com.example.Shelter.bot.handlers.CallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ShelterBot() {
        // Токен и имя будут прочитаны из application.properties
        this.botToken = System.getenv("TELEGRAM_BOT_TOKEN");
        this.botUsername = System.getenv("TELEGRAM_BOT_USERNAME");
    }

    public ShelterBot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
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
                messageHandler.handleMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                callbackHandler.handleCallbackQuery(update.getCallbackQuery());
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
