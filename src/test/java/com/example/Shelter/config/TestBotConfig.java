package com.example.Shelter.config;

import com.example.Shelter.utils.SimpleAbsSenderStub;
import com.example.Shelter.utils.TestMessageSender;
import com.example.Shelter.utils.TestMessageSenderMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@TestConfiguration
public class TestBotConfig {

    @Bean
    public TestMessageSender testMessageSender() {
        return new TestMessageSenderMock();
    }

    @Bean
    public SimpleAbsSenderStub simpleAbsSenderStub() {
        return new SimpleAbsSenderStub();
    }

    // Утилиты для создания тестовых объектов
    public static Update createTextUpdate(Long chatId, Long userId, String text) {
        Update update = new Update();
        update.setMessage(createMessage(chatId, userId, text));
        update.setUpdateId(1);
        return update;
    }

    public static Message createMessage(Long chatId, Long userId, String text) {
        Message message = new Message();

        Chat chat = new Chat();
        chat.setId(chatId);
        chat.setFirstName("TestChat");
        chat.setType("private");

        User from = new User();
        from.setId(userId);
        from.setFirstName("TestUser");
        from.setUserName("testuser");
        from.setIsBot(false);

        message.setChat(chat);
        message.setFrom(from);
        message.setText(text);
        message.setMessageId(1);

        return message;
    }
}
