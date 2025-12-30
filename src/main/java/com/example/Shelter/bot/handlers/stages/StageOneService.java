package com.example.Shelter.bot.handlers.stages;

import com.example.Shelter.model.BotState;
import com.example.Shelter.model.ShelterType;
import com.example.Shelter.model.User;
import com.example.Shelter.service.MenuService;
import com.example.Shelter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class StageOneService {

    @Autowired
    private UserService userService;

    @Autowired
    private StageZeroService stageZeroService;

    @Autowired
    private MenuService menuService;

    // Главное меню, этап 1
    public SendMessage showShelterInfoMenu(User user) {
        user.setCurrentState(BotState.SHELTER_INFO_MENU);
        userService.saveUser(user);

        String shelterType = user.getChosenShelter() == ShelterType.CAT_SHELTER ? "кошачьего" : "собачьего";

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("О приюте");
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Расписание и адрес");
        keyboard.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Контакты охраны");
        keyboard.add(row3);

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Техника безопасности");
        keyboard.add(row4);

        KeyboardRow row5 = new KeyboardRow();
        row5.add("Оставить контакты");
        keyboard.add(row5);

        KeyboardRow row6 = new KeyboardRow();
        row6.add("Позвать волонтера");
        row6.add("Назад");
        keyboard.add(row6);

        ReplyKeyboardMarkup replyMarkup = ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .build();

        String text = String.format("""
            *Информация о %s приюте*
            
            Выберите интересующий вас раздел:
            """, shelterType);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(text)
                .parseMode("Markdown")
                .replyMarkup(replyMarkup)
                .build();
    }

    // Обработка выбора в меню, этап 1
    public SendMessage handleShelterInfoChoice(User user, String text) {
        switch (text) {
            case "О приюте":
                return showAboutShelter(user);
            case "Расписание и адрес":
                return showScheduleAndAddress(user);
            case "Контакты охраны":
                return showSecurityContacts(user);
            case "Техника безопасности":
                return showSafetyRules(user);
            case "Оставить контакты":
                return askForContactInfo(user);
            case "Позвать волонтера":
                return callVolunteer(user);
            case "Назад":
                return goBackToMainMenu(user);
            default:
                return SendMessage.builder()
                        .chatId(user.getChatId())
                        .text("Пожалуйста, выберите один из пунктов меню:")
                        .build();
        }
    }

    // Информация о приюте
    private SendMessage showAboutShelter(User user) {
        user.setCurrentState(BotState.SHELTER_ABOUT);
        userService.saveUser(user);

        String info = "";
        if (user.getChosenShelter() == ShelterType.CAT_SHELTER) {
            info = """
                *Приют для кошек "Мурлыка"*
                
                Наш приют существует с 2010 года. Мы спасаем, лечим и находим дом для бездомных кошек.
                
                *Наша миссия:*
                • Спасение бездомных кошек
                • Лечение и реабилитация
                • Поиск любящих хозяев
                • Кастрация/стерилизация
                
                *Статистика:*
                • Более 5000 спасенных кошек
                • 80% нашли новый дом
                • 20 волонтеров постоянно помогают
                """;
        } else {
            info = """
                *Приют для собак "Верный друг"*
                
                Мы помогаем бездомным собакам с 2008 года. Каждая собака получает медицинскую помощь и заботу.
                
                *Наша миссия:*
                • Спасение собак с улиц
                • Ветеринарная помощь
                • Социализация и дрессировка
                • Ответственное усыновление
                
                *Статистика:*
                • Более 3000 спасенных собак
                • 75% нашли любящих хозяев
                • Собственная площадка для выгула
                """;
        }

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(info)
                .parseMode("Markdown")
                .build();
    }

    // Расписание и адрес
    private SendMessage showScheduleAndAddress(User user) {
        user.setCurrentState(BotState.SHELTER_SCHEDULE);
        userService.saveUser(user);

        String info = "";
        if (user.getChosenShelter() == ShelterType.CAT_SHELTER) {
            info = """
                *Адрес кошачьего приюта:*
                г. Москва, ул. Кошачья, д. 15
                
                *Как добраться:*
                Метро "Котловская", 10 минут пешком
                Автобусы: 123, 456 до остановки "Кошачий приют"
                
                *Расписание работы:*
                Понедельник-Пятница: 10:00 - 18:00
                Суббота: 11:00 - 16:00
                Воскресенье: 11:00 - 15:00
                
                *Телефон:* +7 (495) 123-45-67
                """;
        } else {
            info = """
                *Адрес собачьего приюта:*
                г. Москва, ул. Собачья, д. 20
                
                *Как добраться:*
                Метро "Песчаная", 15 минут пешком
                Автобусы: 789, 101 до остановки "Собачий приют"
                
                *Расписание работы:*
                Понедельник-Пятница: 09:00 - 19:00
                Суббота: 10:00 - 17:00
                Воскресенье: 10:00 - 16:00
                
                *Телефон:* +7 (495) 765-43-21
                """;
        }

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(info)
                .parseMode("Markdown")
                .build();
    }

    // Контакты охраны для пропуска
    private SendMessage showSecurityContacts(User user) {
        user.setCurrentState(BotState.SHELTER_SECURITY_CONTACTS);
        userService.saveUser(user);

        String info = "";
        if (user.getChosenShelter() == ShelterType.CAT_SHELTER) {
            info = """
                *Оформление пропуска на территорию кошачьего приюта*
                
                Для оформления пропуска на автомобиль необходимо:
                1. Предоставить данные автомобиля (гос. номер, марка, модель)
                2. ФИО водителя
                3. Дата и время визита
                
                *Контакты охраны:*
                +7 (495) 111-22-33 (круглосуточно)
                security@catshelter.ru
                
                *Заявку нужно отправлять за 24 часа до визита!*
                """;
        } else {
            info = """
                *Оформление пропуска на территорию собачьего приюта*
                
                Для оформления пропуска на автомобиль необходимо:
                1. Гос. номер автомобиля
                2. ФИО водителя и пассажиров
                3. Цель визита
                4. Дата и время
                
                *Контакты охраны:*
                +7 (495) 444-55-66 (круглосуточно)
                security@dogshelter.ru
                
                *Заявку нужно отправлять минимум за 3 часа до визита!*
                """;
        }

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(info)
                .parseMode("Markdown")
                .build();
    }

    // Техника безопасности
    private SendMessage showSafetyRules(User user) {
        user.setCurrentState(BotState.SHELTER_SAFETY_RULES);
        userService.saveUser(user);

        String info = "";
        if (user.getChosenShelter() == ShelterType.CAT_SHELTER) {
            info = """
                *Правила безопасности в кошачьем приюте*
                
                1. *Общие правила:*
                   • Не берите кошек на руки без разрешения персонала
                   • Не кормите животных без согласования
                   • Дети до 12 лет только в сопровождении взрослых
                
                2. *При общении с кошками:*
                   • Подходите медленно и спокойно
                   • Дайте кошке понюхать вашу руку
                   • Не делайте резких движений
                
                3. *Запрещено:*
                   • Приносить своих животных
                   • Шуметь и бегать
                   • Открывать клетки самостоятельно
                
                4. *В случае укуса/царапины:*
                   • Немедленно сообщите персоналу
                   • Обработайте рану
                   • Зафиксируйте инцидент в журнале
                """;
        } else {
            info = """
                *Правила безопасности в собачьем приюте*
                
                1. *Общие правила:*
                   • Не заходите в вольеры без персонала
                   • Не кормите собак с рук
                   • Держитесь на безопасном расстоянии
                
                2. *При общении с собаками:*
                   • Не смотрите собаке прямо в глаза
                   • Не поворачивайтесь спиной
                   • Двигайтесь спокойно и уверенно
                
                3. *Запрещено:*
                   • Приносить еду для собак
                   • Бегать и кричать
                   • Дразнить животных
                
                4. *В случае опасности:*
                   • Сохраняйте спокойствие
                   • Медленно отступайте
                   • Зовите персонал
                """;
        }

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(info)
                .parseMode("Markdown")
                .build();
    }

    // Запрос контактных данных
    private SendMessage askForContactInfo(User user) {
        user.setCurrentState(BotState.AWAITING_CONTACT_INFO);
        userService.saveUser(user);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text("""
                *Оставьте ваши контактные данные*
                
                Пожалуйста, отправьте ваш номер телефона в формате:
                +7 XXX XXX XX XX
                
                Или нажмите кнопку "Поделиться контактом"
                """)
                .parseMode("Markdown")
                .build();
    }

    // Обработка полученных контактов
    public SendMessage saveContactInfo(User user, String contactInfo) {
        // Здесь сохраняем контакты в базу
        user.setCurrentState(BotState.SHELTER_INFO_MENU);
        userService.saveUser(user);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text("Спасибо! Ваши контакты сохранены. С вами свяжутся в ближайшее время.")
                .build();
    }

    // Вызов волонтера
    private SendMessage callVolunteer(User user) {
        user.setCurrentState(BotState.AWAITING_VOLUNTEER);
        userService.saveUser(user);

        // Здесь логика уведомления волонтеров

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text("""
                *Волонтер уведомлен*
                
                Мы передали ваш запрос волонтеру. Ожидайте ответа в ближайшее время.
                
                Среднее время ответа: 15-30 минут.
                """)
                .parseMode("Markdown")
                .build();
    }

    // Назад в главное меню
    private SendMessage goBackToMainMenu(User user) {
        user.setCurrentState(BotState.MAIN_MENU);
        userService.saveUser(user);
        return menuService.showMainMenu(user);
    }

    private ReplyKeyboardMarkup createShelterKeyboard() {
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Приют для кошек");
        row.add("Приют для собак");
        keyboard.add(row);

        return ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
    }
}
