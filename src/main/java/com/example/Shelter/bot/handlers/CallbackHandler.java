package com.example.Shelter.bot.handlers;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class CallbackHandler {

    public void handleCallbackQuery(CallbackQuery callbackQuery, AbsSender absSender)
            throws TelegramApiException {

        System.out.println("Callback received: " + callbackQuery.getData());

        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQuery.getId());
        answer.setText("Принято");
        answer.setShowAlert(false);

        absSender.execute(answer);

        // Дополнительная логика для приюта
        String data = callbackQuery.getData();
        if (data != null && !data.isEmpty()) {
            // Обработка callback-действий для приюта
            processCallbackForShelter(data, callbackQuery, absSender);
        }
    }

    private void processCallbackForShelter(String data, CallbackQuery callbackQuery,
                                           AbsSender absSender) throws TelegramApiException {
        switch (data) {
            case "adopt_info":
                // Отправляем информацию об усыновлении
                break;
            case "report_reminder":
                // Напоминание об отчете
                break;
            case "volunteer_call":
                // Вызов волонтера
                break;
            // Добавьте другие callback-действия для приюта
        }
    }
}