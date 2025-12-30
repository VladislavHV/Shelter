package com.example.Shelter.service;

import com.example.Shelter.model.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {

    public SendMessage showMainMenu(User user) {
        String shelterName = user.getChosenShelter().getDescription();

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

        String messageText = String.format("Вы выбрали: %s\n\nЧто вас интересует?", shelterName);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(messageText)
                .replyMarkup(replyMarkup)
                .build();
    }
}
