package com.example.Shelter.bot.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CallbackHandlerTest {

    @Mock
    private AbsSender absSender;

    private CallbackHandler callbackHandler = new CallbackHandler();

    @Test
    void handleCallbackQuery_SendsAnswer() throws TelegramApiException {
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("adopt_123");
        callbackQuery.setId("callback_id_123");

        callbackHandler.handleCallbackQuery(callbackQuery, absSender);

        verify(absSender).execute(any(AnswerCallbackQuery.class));
    }

    @Test
    void handleCallbackQuery_WithEmptyData_StillSendsAnswer() throws TelegramApiException {
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData("");
        callbackQuery.setId("callback_id_456");

        callbackHandler.handleCallbackQuery(callbackQuery, absSender);

        verify(absSender).execute(any(AnswerCallbackQuery.class));
    }

    @Test
    void handleCallbackQuery_WithNullData_StillSendsAnswer() throws TelegramApiException {
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(null); // null данные
        callbackQuery.setId("callback_id_789");

        callbackHandler.handleCallbackQuery(callbackQuery, absSender);

        verify(absSender).execute(any(AnswerCallbackQuery.class));
    }
}