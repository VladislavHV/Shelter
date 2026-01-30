package com.example.Shelter.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

// Простой интерфейс для отправки сообщений в тестах
public interface TestMessageSender {

    Message send(SendMessage sendMessage) throws TelegramApiException;

}
