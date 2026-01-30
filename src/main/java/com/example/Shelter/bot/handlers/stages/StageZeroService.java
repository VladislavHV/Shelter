package com.example.Shelter.bot.handlers.stages;

import com.example.Shelter.model.BotState;
import com.example.Shelter.model.ShelterType;
import com.example.Shelter.model.User;
import com.example.Shelter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class StageZeroService {

    @Autowired
    private UserService userService;

    public SendMessage handleStart(User user) {
        user.setBotState(BotState.STAGE_ZERO);
        userService.saveUser(user);

        return createShelterChoiceMessage(user.getChatId());
    }

    // Приветствие нового пользователя
    private SendMessage createShelterChoiceMessage(Long chatId) {
        String text = """
            Добро пожаловать в бот приюта животных!
            
            Я помогу вам:
            • Узнать информацию о приюте
            • Взять животное из приюта
            • Отправить отчет о питомце
            
            Для начала выберите приют:""";

        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(createShelterKeyboard())
                .build();
    }

    // Клавиатура для выбора приюта
    private ReplyKeyboardMarkup createShelterKeyboard() {
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(ShelterType.CAT.getFullName());
        row.add(ShelterType.DOG.getFullName());
        keyboard.add(row);

        return ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
    }

    // Обработка выбора приюта
    public SendMessage handleShelterChoice(User user, String text) {
        ShelterType chosenShelter = null;

        if (text.contains(ShelterType.CAT.getDescription()) ||
                text.contains("кош") || text.contains("кот")) {
            chosenShelter = ShelterType.CAT;
        } else if (text.contains(ShelterType.DOG.getDescription()) ||
                text.contains("собак") || text.contains("пёс")|| text.contains("пес")) {
            chosenShelter = ShelterType.DOG;
        }

        if (chosenShelter == null) {
            return SendMessage.builder()
                    .chatId(user.getChatId())
                    .text("Пожалуйста, выберите приют:")
                    .replyMarkup(createShelterKeyboard())
                    .build();
        }

        user.setSelectedShelter("CAT");
        user.setBotState(BotState.STAGE_ONE);
        userService.saveUser(user);

        return showMainMenu(user);
    }

    // Главное меню после выбора приюта
    public SendMessage showMainMenu(User user) {
        String shelterName = user.getSelectedShelter();

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Информация о приюте");
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Как взять животное");
        keyboard.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Отчет о питомце");
        keyboard.add(row3);

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Позвать волонтера");
        keyboard.add(row4);

        ReplyKeyboardMarkup replyMarkup = ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .build();

        String messageText = String.format("Вы выбрали: %s\n\nЧто вас интересует?",
                user.getSelectedShelter());

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(messageText)
                .replyMarkup(replyMarkup)
                .build();
    }
}
