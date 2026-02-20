package com.example.Shelter.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.Shelter.bot.handlers.MessageHandler;
import com.example.Shelter.bot.handlers.CallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ShelterBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(ShelterBot.class);

    private final String botToken;
    private final String botUsername;

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private CallbackHandler callbackHandler;

    public ShelterBot(
            @Value("${telegram.bot.token:#{null}}") String botToken,
            @Value("${telegram.bot.username:#{null}}") String botUsername) {

        super(resolveToken(botToken));  // Выносим логику в отдельный метод

        this.botToken = resolveToken(botToken);
        this.botUsername = resolveUsername(botUsername);

        System.out.println("Инициализация бота приюта:");
        System.out.println("Режим: " + (botToken == null ? "MOCK" : "REAL"));
        System.out.println("Имя: " + this.botUsername);
    }

    private static String resolveToken(String token) {
        return (token == null || token.isEmpty() || token.contains("placeholder"))
                ? "mock-token-" + System.currentTimeMillis()
                : token;
    }

    private static String resolveUsername(String username) {
        return (username == null || username.isEmpty())
                ? "ShelterMockBot"
                : username;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                Message message = update.getMessage();

                if (message.hasText()) {
                    logger.debug("Текстовое сообщение от {}: {}",
                            message.getChatId(), message.getText());
                    messageHandler.handleMessage(message, this);

                } else if (message.hasPhoto()) {
                    logger.debug("Фото от {}", message.getChatId());
                    handlePhotoMessage(message, this);

                } else if (message.hasDocument()) {
                    logger.debug("Документ от {}", message.getChatId());
                    handleDocumentMessage(message, this);
                }

            } else if (update.hasCallbackQuery()) {
                logger.debug("Callback от {}: {}",
                        update.getCallbackQuery().getFrom().getId(),
                        update.getCallbackQuery().getData());
                callbackHandler.handleCallbackQuery(update.getCallbackQuery(), this);
            }

        } catch (TelegramApiException e) {
            logger.error("Telegram API ошибка: {}", e.getMessage());
            sendErrorMessage(update, "Ошибка связи с Telegram. Попробуйте позже.");
        } catch (Exception e) {
            logger.error("Системная ошибка: {}", e.getMessage(), e);
            sendErrorMessage(update, "Внутренняя ошибка приюта. Волонтеры уведомлены.");
        }
    }

    private void handlePhotoMessage(Message message, ShelterBot bot) throws TelegramApiException {
        Long chatId = message.getChatId();
        logger.info("Обработка фото для отчета от {}", chatId);

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText("Фото получено! Теперь добавьте описание к ежедневному отчету.");
        bot.execute(response);
    }

    private void handleDocumentMessage(Message message, ShelterBot bot) throws TelegramApiException {
        Long chatId = message.getChatId();
        logger.info("Обработка документа от {}", chatId);

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText("Документ получен. Если это анкета на усыновление - спасибо!");
        bot.execute(response);
    }

    private void sendErrorMessage(Update update, String text) {
        try {
            if (update.hasMessage()) {
                Long chatId = update.getMessage().getChatId();
                SendMessage message = new SendMessage();
                message.setChatId(chatId.toString());
                message.setText(text);
                this.execute(message);
                logger.info("Отправлено сообщение об ошибке пользователю {}", chatId);
            }
        } catch (TelegramApiException ex) {
            logger.error("Не удалось отправить сообщение об ошибке: {}", ex.getMessage());
        }
    }

}
