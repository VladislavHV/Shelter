package com.example.Shelter.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

// Простой мок для отправки сообщений в тестах
public class SimpleMockSender {

    private final List<SendMessage> sentMessages = new ArrayList<>();
    private final List<String> actions = new ArrayList<>();

    // "Отправляет" сообщение (сохраняет его для проверки в тестах)
    public Message sendMessage(SendMessage sendMessage) throws TelegramApiException {
        // Сохраняем сообщение
        sentMessages.add(sendMessage);

        // Логируем
        String logEntry = String.format("Sent to %s: %s",
                sendMessage.getChatId(),
                sendMessage.getText() != null && sendMessage.getText().length() > 50 ?
                        sendMessage.getText().substring(0, 50) + "..." : sendMessage.getText()
        );
        actions.add(logEntry);
        System.out.println("[MOCK] " + logEntry);

        // Создаем и возвращаем mock-ответ
        return createMockResponse(sendMessage);
    }

    // Создает mock-ответ на отправленное сообщение
    private Message createMockResponse(SendMessage sendMessage) {
        Message response = new Message();
        response.setText(sendMessage.getText());

        Chat chat = new Chat();

        try {
            String chatIdStr = sendMessage.getChatId();
            if (chatIdStr != null && !chatIdStr.isEmpty()) {
                Long chatIdLong = Long.parseLong(chatIdStr);
                chat.setId(chatIdLong);
            } else {
                chat.setId(0L);
            }
        } catch (NumberFormatException e) {
            chat.setId(0L);
        }

        chat.setTitle("Test Chat");
        chat.setType("private");
        response.setChat(chat);

        // Устанавливаем ID сообщения
        response.setMessageId((int) (Math.random() * 10000));

        // Устанавливаем дату
        response.setDate((int) (System.currentTimeMillis() / 1000));

        return response;
    }

    // Возвращает количество отправленных сообщений
    public int getSentMessageCount() {
        return sentMessages.size();
    }

    // Возвращает список всех отправленных сообщений
    public List<SendMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    // Возвращает тексты всех отправленных сообщений
    public List<String> getSentMessageTexts() {
        List<String> texts = new ArrayList<>();
        for (SendMessage msg : sentMessages) {
            if (msg.getText() != null) {
                texts.add(msg.getText());
            }
        }
        return texts;
    }

    // Проверяет, было ли отправлено сообщение с указанным текстом
    public boolean containsMessageWithText(String expectedText) {
        for (SendMessage msg : sentMessages) {
            if (msg.getText() != null && msg.getText().contains(expectedText)) {
                return true;
            }
        }
        return false;
    }

    // Проверяет, было ли отправлено сообщение в указанный чат
    public boolean containsMessageToChat(String chatId) {
        for (SendMessage msg : sentMessages) {
            if (chatId.equals(msg.getChatId())) {
                return true;
            }
        }
        return false;
    }

    // Проверяет, было ли отправлено сообщение в указанный чат (Long версия)
    public boolean containsMessageToChat(Long chatId) {
        return containsMessageToChat(String.valueOf(chatId));
    }

    // Возвращает первое отправленное сообщение
    public SendMessage getFirstSentMessage() {
        return sentMessages.isEmpty() ? null : sentMessages.get(0);
    }

    // Возвращает последнее отправленное сообщение
    public SendMessage getLastSentMessage() {
        return sentMessages.isEmpty() ? null : sentMessages.get(sentMessages.size() - 1);
    }

    // Очищает историю
    public void clear() {
        sentMessages.clear();
        actions.clear();
    }

    // Выводит все отправленные сообщения
    public void printAllMessages() {
        System.out.println("\n=== SimpleMockSender: Отправленные сообщения ===");
        for (int i = 0; i < sentMessages.size(); i++) {
            SendMessage msg = sentMessages.get(i);
            System.out.printf("%d. To %s: %s%n",
                    i + 1,
                    msg.getChatId(),
                    msg.getText() != null ?
                            (msg.getText().length() > 100 ?
                                    msg.getText().substring(0, 100) + "..." :
                                    msg.getText()) :
                            "[без текста]"
            );
        }
        System.out.println("===========================================\n");
    }
}