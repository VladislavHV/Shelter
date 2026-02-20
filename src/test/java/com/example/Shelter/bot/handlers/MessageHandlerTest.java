package com.example.Shelter.bot.handlers;

import com.example.Shelter.bot.ShelterBot;
import com.example.Shelter.bot.handlers.stages.StageOneService;
import com.example.Shelter.bot.handlers.stages.StageThreeService;
import com.example.Shelter.bot.handlers.stages.StageZeroService;
import com.example.Shelter.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageHandlerTest {

    @Mock
    private ShelterBot bot;

    @Mock
    private UserService userService;

    @Mock
    private StageZeroService stageZeroService;

    @Mock
    private StageOneService stageOneService;

    @Mock
    private StageThreeService stageThreeService;

    private com.example.Shelter.model.User createShelterUser() {
        com.example.Shelter.model.User user = new com.example.Shelter.model.User();
        user.setBotState(com.example.Shelter.model.BotState.CHOOSE_SHELTER);
        return user;
    }

    @Test
    void handleMessage_StartCommand() throws Exception {
        MessageHandler messageHandler = new MessageHandler();

        setField(messageHandler, "userService", userService);
        setField(messageHandler, "stageZeroService", stageZeroService);
        setField(messageHandler, "stageOneService", stageOneService);
        setField(messageHandler, "stageThreeService", stageThreeService);

        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(12345L);
        message.setChat(chat);

        org.telegram.telegrambots.meta.api.objects.User telegramUser =
                new org.telegram.telegrambots.meta.api.objects.User();
        telegramUser.setId(12345L);
        telegramUser.setFirstName("Иван");
        message.setFrom(telegramUser);
        message.setText("/start");

        com.example.Shelter.model.User shelterUser = createShelterUser();

        when(userService.getOrCreateUser(anyLong(), any(org.telegram.telegrambots.meta.api.objects.User.class)))
                .thenReturn(shelterUser);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId("12345");
        sendMessage.setText("Добро пожаловать");

        when(stageZeroService.handleStart(any(com.example.Shelter.model.User.class)))
                .thenReturn(sendMessage);

        messageHandler.handleMessage(message, bot);

        verify(bot).execute(any(SendMessage.class));
    }

    private void setField(Object target, String fieldName, Object value)
            throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

}
