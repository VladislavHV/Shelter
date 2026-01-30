package com.example.Shelter.bot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShelterBotTest {

    @Autowired
    private ShelterBot shelterBot;

    @MockBean // Красное подчеркивание, проблем не вызывает
    private org.telegram.telegrambots.meta.generics.Webhook bot;

    @Test
    void onUpdateReceived_TextMessage_DoesNotThrow() {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(12345L);
        message.setChat(chat);
        message.setText("/start");
        update.setMessage(message);

        assertDoesNotThrow(() -> shelterBot.onUpdateReceived(update));
    }

    @Test
    void getBotUsername_ReturnsCorrectName() {
        assertEquals("ShelterMockBot", shelterBot.getBotUsername());
    }

    @Test
    void getBotToken_ReturnsToken() {
        assertNotNull(shelterBot.getBotToken());
        assertTrue(shelterBot.getBotToken().contains("mock"));
    }

}