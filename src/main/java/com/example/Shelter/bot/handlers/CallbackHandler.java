package com.example.Shelter.bot.handlers;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class CallbackHandler {

    public void handleCallbackQuery(CallbackQuery callbackQuery, AbsSender absSender) throws TelegramApiException {
        System.out.println("Callback received: " + callbackQuery.getData());
    }
}
