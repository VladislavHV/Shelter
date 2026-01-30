package com.example.Shelter.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class TestMessageSenderMock implements TestMessageSender {

    private final List<SendMessage> sentMessages = new ArrayList<>();
    private final List<String> actions = new ArrayList<>();

    @Override
    public Message send(SendMessage sendMessage) throws TelegramApiException {
        // Сохраняем сообщение
        sentMessages.add(sendMessage);

        // Логируем
        String logEntry = String.format("Sent to %s: %s",
                sendMessage.getChatId(),
                sendMessage.getText() != null && sendMessage.getText().length() > 50 ?
                        sendMessage.getText().substring(0, 50) + "..." : sendMessage.getText()
        );
        actions.add(logEntry);
        System.out.println("[TEST MOCK] " + logEntry);

        // Возвращаем простую заглушку
        Message mockResponse = new Message();
        mockResponse.setText(sendMessage.getText());

        // Не устанавливаем chat - для тестов это не обязательно
        return mockResponse;
    }

    public int getSentMessageCount() {
        return sentMessages.size();
    }

    public List<SendMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public List<String> getSentMessageTexts() {
        List<String> texts = new ArrayList<>();
        for (SendMessage msg : sentMessages) {
            if (msg.getText() != null) {
                texts.add(msg.getText());
            }
        }
        return texts;
    }

    public boolean containsMessageWithText(String expectedText) {
        return getSentMessageTexts().stream()
                .anyMatch(text -> text.contains(expectedText));
    }

    public void clear() {
        sentMessages.clear();
        actions.clear();
    }
}