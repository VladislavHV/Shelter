package com.example.Shelter.bot.handlers;

import com.example.Shelter.bot.handlers.stages.StageOneService;
import com.example.Shelter.bot.handlers.stages.StageThreeService;
import com.example.Shelter.bot.handlers.stages.StageZeroService;
import com.example.Shelter.model.BotState;
import com.example.Shelter.model.User;
import com.example.Shelter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class MessageHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private StageZeroService stageZeroService;

    @Autowired
    private StageOneService stageOneService;

    @Autowired
    private StageThreeService stageThreeService;

    public MessageHandler() {
    }

    @Autowired
    public MessageHandler(StageOneService stageOneService, StageThreeService stageThreeService, StageZeroService stageZeroService, UserService userService) {
        this.stageOneService = stageOneService;
        this.stageThreeService = stageThreeService;
        this.stageZeroService = stageZeroService;
        this.userService = userService;
    }

    public void handleMessage(Message message, AbsSender absSender) throws TelegramApiException {
        Long chatId = message.getChatId();
        String text = message.getText();
        User user = userService.getOrCreateUser(chatId, message.getFrom());

        BotState currentState = user.getBotState();

        // Обработка команды /start
        if ("/start".equals(text)) {
            SendMessage response = stageZeroService.handleStart(user);
            absSender.execute(response);
            return;
        }

        // Обработка по состояниям
        switch (currentState) {
            case CHOOSE_SHELTER:
                SendMessage shelterResponse = stageZeroService.handleShelterChoice(user, text);
                absSender.execute(shelterResponse);
                break;

            case MAIN_MENU:
                handleMainMenuChoice(user, text, absSender);
                break;

            case SHELTER_INFO_MENU:
                SendMessage infoResponse = stageOneService.handleShelterInfoChoice(user, text);
                absSender.execute(infoResponse);
                break;

            default:
                SendMessage defaultResponse = SendMessage.builder()
                        .chatId(chatId)
                        .text("Пожалуйста, используйте команду /start для начала работы.")
                        .build();
                absSender.execute(defaultResponse);
        }
    }

    private void handleMainMenuChoice(User user, String text, AbsSender absSender) throws TelegramApiException {
        SendMessage response;

        switch (text) {
            case "Информация о приюте":
                response = stageOneService.showShelterInfoMenu(user);
                break;

            case "Как взять животное":
                response = SendMessage.builder()
                        .chatId(user.getChatId())
                        .text("Этап 2 - Как взять животное")
                        .build();
                break;

            case "Отчет о питомце":
                response = SendMessage.builder()
                        .chatId(user.getChatId())
                        .text("Этап 3 - Отчет о питомце")
                        .build();
                break;

            case "Позвать волонтера":
                response = SendMessage.builder()
                        .chatId(user.getChatId())
                        .text("Волонтер уведомлен. Ожидайте ответа.")
                        .build();
                break;

            case "Посмотреть мои отчеты":
                response = stageThreeService.showUserReports1(user);
                break;

            default:
                response = SendMessage.builder()
                        .chatId(user.getChatId())
                        .text("Пожалуйста, выберите один из пунктов меню:")
                        .build();
        }

        absSender.execute(response);
    }
}