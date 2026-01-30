package com.example.Shelter.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Обертка вокруг AbsSender для тестирования
public class MockAbsSender {

    private final List<SendMessage> sentMessages = new ArrayList<>();
    private final List<String> actions = new ArrayList<>();
    private final AbsSender realSender; // опционально

    public MockAbsSender() {
        this.realSender = null;
    }

    public MockAbsSender(AbsSender realSender) {
        this.realSender = realSender;
    }

    public Message execute(SendMessage sendMessage) throws TelegramApiException {
        actions.add("Sent: " + sendMessage.getText());
        sentMessages.add(sendMessage);

        // Если есть реальный sender, используем его
        if (realSender != null) {
            return realSender.execute(sendMessage);
        }

        // Иначе создаем mock-ответ
        Message mockMessage = new Message();
        mockMessage.setText(sendMessage.getText());
        mockMessage.setMessageId((int) (Math.random() * 1000));

        Chat chat = new Chat();
        chat.setId(Long.valueOf(sendMessage.getChatId()));
        mockMessage.setChat(chat);

        return mockMessage;
    }

    public List<SendMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public List<String> getActions() {
        return new ArrayList<>(actions);
    }

    public List<String> getSentMessageTexts() {
        List<String> texts = new ArrayList<>();
        for (SendMessage msg : sentMessages) {
            texts.add(msg.getText());
        }
        return texts;
    }

    public void clear() {
        sentMessages.clear();
        actions.clear();
    }

    public int getSentMessageCount() {
        return sentMessages.size();
    }

    public Optional<Object> getExecutedActions() {
        return Optional.empty();
    }
}
