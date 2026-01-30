package com.example.Shelter.utils;

import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

// Простой адаптер, который не наследует AbsSender, а реализует только нужные методы
public class SimpleAbsSenderStub {

    private final TestMessageSenderMock mockSender;

    public SimpleAbsSenderStub() {
        this.mockSender = new TestMessageSenderMock();
    }

    // Метод, который можно передать в handleMessage вместо AbsSender
    public Object getAsAbsSender() {
        return new Object() {
            public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method)
                    throws TelegramApiException {
                if (method instanceof SendMessage) {
                    return (T) mockSender.send((SendMessage) method);
                }
                return null;
            }

            public Boolean execute(BotApiObject botApiObject) throws TelegramApiException {
                return true;
            }
        };
    }

    // Методы для проверки в тестах
    public int getSentMessageCount() {
        return mockSender.getSentMessageCount();
    }

    public boolean containsMessageWithText(String text) {
        return mockSender.containsMessageWithText(text);
    }

    public void clear() {
        mockSender.clear();
    }
}
