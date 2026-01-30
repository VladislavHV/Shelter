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
public class StageTwoService {

    @Autowired
    private UserService userService;

    @Autowired
    private StageZeroService stageZeroService;

    // Главное меню Этапа 2
    public SendMessage showAdoptionInfoMenu(User user) {
        user.setBotState(BotState.ADOPTION_INFO_MENU);
        userService.saveUser(user);

        boolean isCatShelter = "CAT".equals(user.getSelectedShelter());
        String animalType = isCatShelter ? "кошку/кота" : "собаку";

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Правила знакомства");
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Необходимые документы");
        keyboard.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Транспортировка");
        keyboard.add(row3);

        KeyboardRow row4 = new KeyboardRow();
        if (isCatShelter) {
            row4.add("Дом для котенка");
            row4.add("Дом для взрослой кошки");
        } else {
            row4.add("Дом для щенка");
            row4.add("Дом для взрослой собаки");
        }
        keyboard.add(row4);

        KeyboardRow row5 = new KeyboardRow();
        row5.add("Дом для особенного питомца");
        keyboard.add(row5);

        if (!isCatShelter) {
            KeyboardRow row6 = new KeyboardRow();
            row6.add("Советы кинолога");
            row6.add("Рекомендации кинологов");
            keyboard.add(row6);
        }

        KeyboardRow lastRow = new KeyboardRow();
        lastRow.add("Причины отказа");
        lastRow.add("Контакты для усыновления");
        keyboard.add(lastRow);

        KeyboardRow backRow = new KeyboardRow();
        backRow.add("Позвать волонтера");
        backRow.add("Назад");
        keyboard.add(backRow);

        ReplyKeyboardMarkup replyMarkup = ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .build();

        String text = String.format("""
            *Как взять %s из приюта*
            
            Здесь вы найдете всю информацию для подготовки к усыновлению.
            Выберите интересующий раздел:
            """, animalType);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(text)
                .parseMode("Markdown")
                .replyMarkup(replyMarkup)
                .build();
    }

    // Обработка выбора в меню Этапа 2
    public SendMessage handleAdoptionInfoChoice(User user, String text) {
        switch (text) {
            case "Правила знакомства":
                return showMeetingRules(user);
            case "Необходимые документы":
                return showRequiredDocuments(user);
            case "Транспортировка":
                return showTransportRules(user);
            case "Дом для котенка":
            case "Дом для щенка":
                return showPuppyHomePrep(user);
            case "Дом для взрослой кошки":
            case "Дом для взрослой собаки":
                return showAdultHomePrep(user);
            case "Дом для особенного питомца":
                return showDisabledHomePrep(user);
            case "Советы кинолога":
                return showDogTrainerTips(user);
            case "Рекомендации кинологов":
                return showDogTrainerRecommendations(user);
            case "Причины отказа":
                return showRejectionReasons(user);
            case "Контакты для усыновления":
                return askForAdoptionContacts(user);
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

    // Правила знакомства
    private SendMessage showMeetingRules(User user) {
        user.setBotState(BotState.ADOPTION_INFO_MENU);
        userService.saveUser(user);

        String info = "";
        if ("CAT".equals(user.getSelectedShelter())) {
            info = """
                *Правила знакомства с кошкой*
                
                1. *Подготовка:*
                   • Запишитесь на встречу заранее
                   • Приходите без своих животных
                   • Возьмите с собой угощения (после согласования)
                
                2. *Первая встреча:*
                   • Не берите кошку сразу на руки
                   • Дайте ей привыкнуть к вашему запаху
                   • Говорите спокойным голосом
                
                3. *Несколько встреч:*
                   • Рекомендуем 2-3 встречи до решения
                   • Понаблюдайте за поведением в разных ситуациях
                   • Обсудите с персоналом особенности характера
                
                4. *Что взять с собой:*
                   • Переноску (на случай, если решите забрать)
                   • Лакомства (утвержденные приютом)
                   • Фотоаппарат для памятных фото
                """;
        } else {
            info = """
                *Правила знакомства с собакой*
                
                1. *Подготовка:*
                   • Запишитесь на встречу минимум за день
                   • Не кормите свою собаку перед визитом
                   • Наденьте удобную одежду и обувь
                
                2. *Первая встреча на территории:*
                   • Прогулка с собакой в сопровождении волонтера
                   • Наблюдение за реакцией на команды
                   • Проверка контакта "глаза в глаза"
                
                3. *Несколько встреч:*
                   • Минимум 2 встречи обязательны
                   • Тест на совместимость с членами семьи
                   • Совместная прогулка за пределами приюта
                
                4. *Что взять с собой:*
                   • Поводок и ошейник (или шлейку)
                   • Воду и миску для воды
                   • Любимые игрушки собаки (после согласования)
                """;
        }

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(info)
                .parseMode("Markdown")
                .build();
    }

    // Необходимые документы
    private SendMessage showRequiredDocuments(User user) {
        user.setBotState(BotState.ADOPTION_INFO_MENU);
        userService.saveUser(user);

        String info = """
            *Документы для усыновления*
            
            *Обязательные документы:*
            1. Паспорт гражданина РФ
            2. Документ, подтверждающий право собственности на жилье
               или договор аренды (если снимаете)
            
            *Дополнительно:*
            3. Справка о доходах (необязательно, но желательно)
            4. Письменное согласие всех членов семьи
            5. Анкета усыновителя (выдается в приюте)
            
            *Процесс оформления:*
            • Заполнение анкеты
            • Собеседование с волонтером
            • Подписание договора ответственности
            • Оплата символического взноса (1000-3000 руб.)
            
            *Договор включает:*
            • Обязательства по уходу
            • Право приюта на проверки
            • Условия возврата животного
            """;

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(info)
                .parseMode("Markdown")
                .build();
    }

    // Правила транспортировки
    private SendMessage showTransportRules(User user) {
        user.setBotState(BotState.ADOPTION_INFO_MENU);
        userService.saveUser(user);

        String info = "";
        if ("CAT".equals(user.getSelectedShelter())) {
            info = """
                *Транспортировка кошки*
                
                1. *Переноска:*
                   • Жесткая пластиковая переноска с решеткой
                   • Размер: кошка должна стоять в полный рост
                   • Постелите впитывающую пеленку
                
                2. *Подготовка:*
                   • Не кормите за 2-3 часа до поездки
                   • Положите знакомую игрушку или вещь с запахом приюта
                   • Закройте переноску тканью для успокоения
                
                3. *В машине:*
                   • Закрепите переноску ремнем безопасности
                   • Не открывайте в движении
                   • Поддерживайте комфортную температуру
                
                4. *Успокоение:*
                   • Говорите спокойным голосом
                   • Включите тихую музыку
                   • Делайте остановки при длительной поездке
                """;
        } else {
            info = """
                *Транспортировка собаки*
                
                1. *В машине:*
                   • Используйте специальную решетку или клетку
                   • Маленьких собак - в переноске на заднем сиденье
                   • Пристегните шлейку ремнем безопасности
                
                2. *Подготовка:*
                   • Прогулка и туалет перед поездкой
                   • Легкий корм за 4 часа до выезда
                   • Возьмите воду и миску
                
                3. *Безопасность:*
                   • Не оставляйте в машине одну
                   • Кондиционер в жаркую погоду
                   • Остановки каждые 2-3 часа
                
                4. *Успокоение:*
                   • Знакомая игрушка или лежанка
                   • Успокаивающие феромоны (спрей)
                   • Ласковый разговор
                """;
        }

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(info)
                .parseMode("Markdown")
                .build();
    }

    // Подготовка дома для щенка/котенка
    private SendMessage showPuppyHomePrep(User user) {
        user.setBotState(BotState.ADOPTION_INFO_MENU);
        userService.saveUser(user);

        boolean isCat = "CAT".equals(user.getSelectedShelter());
        String animal = isCat ? "котенка" : "щенка";
        String litter = isCat ? "лоток с наполнителем" : "пеленки";

        String info = String.format("""
            *Подготовка дома для %s*
            
            1. *Безопасность помещения:*
               • Уберите провода и мелкие предметы
               • Закройте розетки заглушками
               • Установите защиту на окна и балкон
            
            2. *Место для отдыха:*
               • Уютный домик или лежанка
               • В тихом, не проходном месте
               • С знакомой подстилкой из приюта
            
            3. *Питание и вода:*
               • Керамические миски (не пластиковые)
               • Поилка-фонтанчик для кошек
               • Еда, к которой привык в приюте
            
            4. *Туалет:*
               • %s в спокойном месте
               • На начальном этапе - несколько лотков/пеленок
               • Средства для уборки (энзимные очистители)
            
            5. *Игрушки и развлечения:*
               • Игрушки для умственной нагрузки
               • Когтеточка для кошек
               • Мячики и пищалки для собак
            
            6. *Первые дни:*
               • Ограничьте пространство одной комнатой
               • Не навязывайте общение
               • Установите режим кормления и прогулок
            """, animal, litter);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(info)
                .parseMode("Markdown")
                .build();
    }

    // Подготовка дома для взрослого животного
    private SendMessage showAdultHomePrep(User user) {
        user.setBotState(BotState.ADOPTION_INFO_MENU);
        userService.saveUser(user);

        boolean isCat = "CAT".equals(user.getSelectedShelter());
        String animal = isCat ? "взрослой кошки" : "взрослой собаки";

        String info = String.format("""
            *Подготовка дома для %s*
            
            1. *Адаптация:*
               • Выделите отдельную комнату на первые дни
               • Не меняйте резко корм
               • Сохраняйте привычный режим
            
            2. *Личное пространство:*
               • Убежище, где можно спрятаться
               • Высокие полки для кошек
               • Свой угол с лежанкой
            
            3. *Безопасность:*
               • Проверьте забор/балкон
               • Уберите токсичные растения
               • Спрячьте лекарства и химикаты
            
            4. *Для кошек дополнительно:*
               • Вертикальное пространство
               • Несколько лотков в разных местах
               • Феромоновый диффузор Feliway
            
            5. *Для собак дополнительно:*
               • Место для прогулок рядом с домом
               • Социальные контакты с другими собаками
               • Курс занятий с кинологом
            
            6. *Терпение:*
               • Адаптация занимает от 2 недель до 3 месяцев
               • Возможны поведенческие проблемы
               • Консультация зоопсихолога при необходимости
            """, animal);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(info)
                .parseMode("Markdown")
                .build();
    }

    // Подготовка дома для животного с ограниченными возможностями
    private SendMessage showDisabledHomePrep(User user) {
        user.setBotState(BotState.ADOPTION_INFO_MENU);
        userService.saveUser(user);

        String info = """
            *Подготовка дома для питомца с ограниченными возможностями*
            
            1. *Для животных с проблемами передвижения:*
               • Накладки на лапы для защиты
               • Пандусы вместо ступенек
               • Нескользкие покрытия на полу
               • Подвижная поддержка при ходьбе
            
            2. *Для слепых животных:*
               • Не перемещайте мебель без необходимости
               • Звуковые сигналы для ориентации
               • Ограждение опасных зон
               • Тактильные указатели (разные покрытия)
            
            3. *Для глухих животных:*
               • Визуальные сигналы вместо звуковых
               • Фонарик для привлечения внимания
               • Вибрационный ошейник для вызова
            
            4. *Медицинский уход:*
               • Регулярные визиты к ветеринару
               • Физиотерапия и массаж
               • Специальные корма и добавки
            
            5. *Психологическая поддержка:*
               • Больше тактильного контакта
               • Поощрение за маленькие успехи
               • Терпение и понимание
            
            6. *Оборудование:*
               • Инвалидные коляски для животных
               • Подъемники для кроватей/диванов
               • Специальные туалеты
            """;

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(info)
                .parseMode("Markdown")
                .build();
    }

    // Советы кинолога (только для собак)
    private SendMessage showDogTrainerTips(User user) {
        if ("CAT".equals(user.getSelectedShelter())) {
                return SendMessage.builder()
                        .chatId(user.getChatId())
                        .text("Эта информация доступна только для собачьего приюта.")
                        .build();
        }


        user.setBotState(BotState.ADOPTION_INFO_MENU);
        userService.saveUser(user);

        String info = """
            *Советы кинолога по первичному общению с собакой*
            
            1. *Первые минуты дома:*
               • Войдите в дом первым
               • Покажите собаке ее место
               • Не навязывайте внимание
            
            2. *Установление контакта:*
               • Говорите спокойным, уверенным голосом
               • Избегайте прямого взгляда в глаза
               • Приседайте на уровень собаки
            
            3. *Основные команды (первые дни):*
               • "Место" - для обозначения своей территории
               • "Ко мне" - без негативных ассоциаций
               • "Фу" - только в опасных ситуациях
            
            4. *Что нельзя делать:*
               • Не кричать и не бить
               • Не будить резко
               • Не отнимать еду и игрушки
            
            5. *Поощрение:*
               • Лакомства за правильное поведение
               • Похвала голосом и поглаживанием
               • Игра как награда
            
            6. *Распространенные ошибки:*
               • Разрешать спать на кровати с первого дня
               • Кормить со стола
               • Игнорировать рычание
            """;

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(info)
                .parseMode("Markdown")
                .build();
    }

    // Рекомендации кинологов (только для собак)
    private SendMessage showDogTrainerRecommendations(User user) {
        if ("CAT".equals(user.getSelectedShelter())) {
            return SendMessage.builder()
                    .chatId(user.getChatId())
                    .text("Эта информация доступна только для собачьего приюта.")
                    .build();
        }

        user.setBotState(BotState.ADOPTION_INFO_MENU);
        userService.saveUser(user);

        String info = """
            *Рекомендации по проверенным кинологам*
            
            *Москва:*
            1. *Кинологический центр "Альфа"*
               • Адрес: ул. Собачья, 10
               • Телефон: +7 (495) 111-22-33
               • Специализация: коррекция поведения
            
            2. *Школа дрессировки "Верный друг"*
               • Адрес: пр. Кинологический, 15
               • Телефон: +7 (495) 222-33-44
               • Специализация: ОКД, аджилити
            
            3. *Центр зоопсихологии "Понимание"*
               • Адрес: ул. Дрессировочная, 5
               • Телефон: +7 (495) 333-44-55
               • Специализация: работа с травмированными собаками
            
            *Онлайн-консультации:*
            4. *Кинолог Иванов И.И.*
               • Сайт: dog-trainer.ru
               • Телефон: +7 (900) 123-45-67
               • Формат: видеоуроки, онлайн-консультации
            
            *Как выбрать кинолога:*
            • Проверьте дипломы и сертификаты
            • Посетите пробное занятие
            • Пообщайтесь с бывшими клиентами
            • Убедитесь в гуманных методах работы
            """;

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(info)
                .parseMode("Markdown")
                .build();
    }

    // Причины отказа
    private SendMessage showRejectionReasons(User user) {
        user.setBotState(BotState.ADOPTION_INFO_MENU);
        userService.saveUser(user);

        boolean isCat = "CAT".equals(user.getSelectedShelter());
        String animal = isCat ? "кошку" : "собаку";

        String info = String.format("""
            *Причины, по которым могут отказать в усыновлении %s*
            
            *Основные причины отказа:*
            
            1. *Жилищные условия:*
               • Аренда жилья без согласия владельца
               • Частые переезды
               • Отсутствие безопасного пространства
            
            2. *Финансовые вопросы:*
               • Нестабильный доход
               • Нежелание тратить на ветеринара
               • Отказ от символического взноса
            
            3. *Семейная ситуация:*
               • Разногласия между членами семьи
               • Маленькие дети без опыта общения с животными
               • Аллергия у кого-то из домочадцев
            
            4. *Опыт и отношение:*
               • Отказ от предыдущих животных
               • Желание подарить животное
               • Восприятие как "игрушки" для детей
            
            5. *Для собак дополнительно:*
               • Отсутствие времени на прогулки
               • Неготовность к дрессировке
               • Отказ от стерилизации/кастрации
            
            6. *Красные флаги:*
               • Агрессивное поведение на собеседовании
               • Сокрытие информации
               • Неявка на повторные встречи
            
            *Что повышает шансы на одобрение:*
            • Честность и открытость
            • Готовность учиться
            • Стабильность в жизни
            • Любовь к животным
            """, animal);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(info)
                .parseMode("Markdown")
                .build();
    }

    // Запрос контактов для усыновления
    private SendMessage askForAdoptionContacts(User user) {
        user.setBotState(BotState.ADOPTION_INFO_MENU);
        userService.saveUser(user);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text("""
                *Контактные данные для усыновления*
                
                Для начала процесса усыновления нам нужна ваша контактная информация:
                
                1. *ФИО полностью*
                2. *Номер телефона*
                3. *Email для связи*
                4. *Адрес проживания*
                5. *Предпочтительное время для звонка*
                
                Отправьте информацию в любом формате, и мы свяжемся с вами для записи на собеседование.
                """)
                .parseMode("Markdown")
                .build();
    }

    // Вызов волонтера
    private SendMessage callVolunteer(User user) {
        user.setBotState(BotState.ADOPTION_INFO_MENU);
        userService.saveUser(user);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text("Волонтер уведомлен. Ожидайте ответа в течение 30 минут.")
                .build();
    }

    // Назад в главное меню
    private SendMessage goBackToMainMenu(User user) {
        return stageZeroService.showMainMenu(user);
    }

}
