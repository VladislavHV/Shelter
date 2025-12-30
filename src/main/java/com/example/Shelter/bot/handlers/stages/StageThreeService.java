package com.example.Shelter.bot.handlers.stages;

import com.example.Shelter.model.*;
import com.example.Shelter.service.ProbationService;
import com.example.Shelter.service.ReportService;
import com.example.Shelter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StageThreeService {

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ProbationService probationService;

    // Главное меню Этапа 3
    public SendMessage showReportMenu(User user) {
        // Проверяем, есть ли у пользователя активный испытательный срок
        Optional<ProbationPeriod> probation = probationService.getActiveProbation(user);

        if (probation.isEmpty()) {
            return SendMessage.builder()
                    .chatId(user.getChatId())
                    .text("""
                    *Отчеты о питомце*
                    
                    У вас нет активного испытательного срока.
                    Отчеты отправляются только во время испытательного срока.
                    
                    Если вы стали усыновителем, попросите волонтера добавить вас в базу.
                    """)
                    .parseMode("Markdown")
                    .build();
        }

        user.setCurrentState(BotState.REPORT_MENU);
        userService.saveUser(user);

        ProbationPeriod prob = probation.get();
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), prob.getEndDate());
        boolean hasReportToday = reportService.hasReportForDate(user, LocalDate.now());

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        if (!hasReportToday) {
            row1.add("Отправить отчет за сегодня");
        } else {
            row1.add("Изменить сегодняшний отчет");
        }
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Отправить отчет за другую дату");
        keyboard.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Посмотреть мои отчеты");
        keyboard.add(row3);

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Статус испытательного срока");
        keyboard.add(row4);

        KeyboardRow row5 = new KeyboardRow();
        row5.add("Позвать волонтера");
        row5.add("Назад");
        keyboard.add(row5);

        ReplyKeyboardMarkup replyMarkup = ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .build();

        String statusText = hasReportToday ? "Сегодняшний отчет отправлен" : "Сегодняшний отчет не отправлен";

        String text = String.format("""
            *Ежедневные отчеты*
            
            *Испытательный срок:*
            Начало: %s
            Конец: %s
            Осталось дней: %d
            
            *Статус:* %s
            
            Выберите действие:
            """,
                prob.getStartDate(),
                prob.getEndDate(),
                daysLeft,
                statusText);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(text)
                .parseMode("Markdown")
                .replyMarkup(replyMarkup)
                .build();
    }

    // Обработка выбора в меню отчетов
    public SendMessage handleReportChoice(User user, String text) {
        switch (text) {
            case "Отправить отчет за сегодня":
            case "Изменить сегодняшний отчет":
                return startReportForm(user, LocalDate.now());
            case "Отправить отчет за другую дату":
                return askForReportDate(user);
            case "Посмотреть мои отчеты":
                return showUserReports(user);
            case "Статус испытательного срока":
                return showProbationStatus(user);
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

    // Начало заполнения формы отчета
    private SendMessage startReportForm(User user, LocalDate reportDate) {
        user.setCurrentState(BotState.REPORT_FORM);
        userService.saveUser(user);

        // Проверяем существующий отчет
        Optional<Report> existingReport = reportService.getReportForDate(user, reportDate);

        if (existingReport.isPresent()) {
            Report report = existingReport.get();
            return SendMessage.builder()
                    .chatId(user.getChatId())
                    .text(String.format("""
                    *Редактирование отчета за %s*
                    
                    Текущие данные:
                    
                    *Фото:* %s
                    *Рацион:* %s
                    *Самочувствие:* %s
                    *Изменения в поведении:* %s
                    
                    Начнем с начала. Пожалуйста, отправьте фото питомца.
                    """,
                            reportDate,
                            report.getPhotoPath() != null ? "отправлено" : "отсутствует",
                            report.getDiet() != null ? report.getDiet().substring(0, Math.min(50, report.getDiet().length())) + "..." : "не указано",
                            report.getWellbeing() != null ? report.getWellbeing().substring(0, Math.min(50, report.getWellbeing().length())) + "..." : "не указано",
                            report.getBehaviorChanges() != null ? report.getBehaviorChanges().substring(0, Math.min(50, report.getBehaviorChanges().length())) + "..." : "не указано"
                    ))
                    .parseMode("Markdown")
                    .build();
        }

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(String.format("""
                *Ежедневный отчет за %s*
                
                Пожалуйста, заполните отчет по шагам.
                
                *Шаг 1 из 4:* Отправьте фото вашего питомца.
                
                Требования к фото:
                • Хорошее освещение
                • Видно состояние животного
                • Желательно без фильтров
                """, reportDate))
                .parseMode("Markdown")
                .build();
    }

    // Запрос даты для отчета
    private SendMessage askForReportDate(User user) {
        return SendMessage.builder()
                .chatId(user.getChatId())
                .text("""
                *Выбор даты для отчета*
                
                Пожалуйста, введите дату в формате ДД.ММ.ГГГГ
                Например: 15.01.2024
                
                Можно отправить отчет за любой день испытательного срока.
                """)
                .parseMode("Markdown")
                .build();
    }

    // Обработка фото отчета
    public SendMessage handleReportPhoto(User user, String photoId) {
        LocalDate reportDate = LocalDate.now(); // В реальности нужно хранить дату отчета в контексте пользователя

        Report report = reportService.getOrCreateReport(user, reportDate);
        report.setPhotoPath(photoId);
        reportService.saveReport(report);

        user.setCurrentState(BotState.AWAITING_REPORT_DIET);
        userService.saveUser(user);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text("""
                *Фото принято!*
                
                *Шаг 2 из 4:* Опишите рацион питомца за сегодня.
                
                Что нужно указать:
                • Что и сколько ел
                • Пил ли воду
                • Любимые лакомства
                • Проблемы с аппетитом
                
                *Пример:*
                "Утром 100г сухого корма Royal Canin, вечером 80г влажного корма. Воду пьет хорошо. Давал лакомство Dentastix 1 шт. Аппетит нормальный."
                """)
                .parseMode("Markdown")
                .build();
    }

    // Обработка описания рациона
    public SendMessage handleReportDiet(User user, String diet) {
        LocalDate reportDate = LocalDate.now();
        Report report = reportService.getReportForDate(user, reportDate).orElseThrow();
        report.setDiet(diet);
        reportService.saveReport(report);

        user.setCurrentState(BotState.AWAITING_REPORT_WELLBEING);
        userService.saveUser(user);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text("""
                *Рацион записан!*
                
                *Шаг 3 из 4:* Опишите общее самочувствие и привыкание.
                
                Что нужно указать:
                • Активность (вялый/игривый)
                • Сон (сколько спит, как)
                • Аппетит и жажда
                • Стул и мочеиспускание
                • Признаки стресса
                
                *Пример:*
                "Активный, играл с мячиком. Спал 4 раза по 1-2 часа. Аппетит хороший. Стул нормальный, 2 раза в день. Адаптируется хорошо, уже освоился в доме."
                """)
                .parseMode("Markdown")
                .build();
    }

    // Обработка описания самочувствия
    public SendMessage handleReportWellbeing(User user, String wellbeing) {
        LocalDate reportDate = LocalDate.now();
        Report report = reportService.getReportForDate(user, reportDate).orElseThrow();
        report.setWellbeing(wellbeing);
        reportService.saveReport(report);

        user.setCurrentState(BotState.AWAITING_REPORT_BEHAVIOR);
        userService.saveUser(user);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text("""
                *Самочувствие записано!*
                
                *Шаг 4 из 4:* Опишите изменения в поведении.
                
                Что нужно указать:
                • Новые привычки (хорошие и плохие)
                • Реакция на команды/зов
                • Отношение к домочадцам
                • Страхи и фобии
                • Успехи в обучении
                
                *Пример:*
                "Научился команде 'сидеть'. Перестал бояться пылесоса. Стал спать в своей лежанке (раньше спал под диваном). Хорошо реагирует на имя."
                """)
                .parseMode("Markdown")
                .build();
    }

    // Завершение отчета
    public SendMessage handleReportBehavior(User user, String behavior) {
        LocalDate reportDate = LocalDate.now();
        Report report = reportService.getReportForDate(user, reportDate).orElseThrow();
        report.setBehaviorChanges(behavior);
        report.setStatus(ReportStatus.PENDING);
        report.setCreatedAt(LocalDateTime.now());
        reportService.saveReport(report);

        user.setCurrentState(BotState.REPORT_MENU);
        userService.saveUser(user);

        // Отправляем уведомление волонтерам
        notifyVolunteersAboutNewReport(report);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text("""
                *Отчет успешно отправлен!*
                
                *Дата:* %s
                *Статус:* Ожидает проверки волонтером
                
                Волонтеры проверяют отчеты после 21:00. Если будут замечания, мы свяжемся с вами.
                
                Спасибо за ответственность!
                """.formatted(reportDate))
                .parseMode("Markdown")
                .build();
    }

    // Показ отчетов пользователя
    private SendMessage showUserReports(User user) {
        List<Report> reports = reportService.getUserReports(user);

        if (reports.isEmpty()) {
            return SendMessage.builder()
                    .chatId(user.getChatId())
                    .text("У вас пока нет отправленных отчетов.")
                    .build();
        }

        StringBuilder message = new StringBuilder();
        message.append("*Ваши отчеты:*\n\n");

        for (Report report : reports) {
            String statusEmoji = switch (report.getStatus()) {
                case APPROVED -> "готово";
                case PENDING -> "ожидайте";
                case NEEDS_IMPROVEMENT -> "ошибка";
                case LATE -> "время";
                case MISSING -> "отказ";
            };

            message.append(String.format("""
                *%s %s*
                Фото: %s
                Статус: %s
                %s
                
                """,
                    report.getReportDate(),
                    statusEmoji,
                    report.getPhotoPath() != null ? "принят" : "отказ",
                    report.getStatus(),
                    report.getVolunteerFeedback() != null ?
                            "Комментарий волонтера: " + report.getVolunteerFeedback() : ""
            ));
        }

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(message.toString())
                .parseMode("Markdown")
                .build();
    }

    // Показ статуса испытательного срока
    private SendMessage showProbationStatus(User user) {
        Optional<ProbationPeriod> probation = probationService.getActiveProbation(user);

        if (probation.isEmpty()) {
            return SendMessage.builder()
                    .chatId(user.getChatId())
                    .text("У вас нет активного испытательного срока.")
                    .build();
        }

        ProbationPeriod prob = probation.get();
        long daysTotal = ChronoUnit.DAYS.between(prob.getStartDate(), prob.getEndDate());
        long daysPassed = ChronoUnit.DAYS.between(prob.getStartDate(), LocalDate.now());
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), prob.getEndDate());

        int reportsSubmitted = reportService.getReportsCount(user, prob.getStartDate(), prob.getEndDate());
        int reportsMissed = (int) (daysPassed - reportsSubmitted);

        String statusMessage = switch (prob.getStatus()) {
            case ACTIVE -> "Активен";
            case EXTENDED_14 -> "Продлен на 14 дней";
            case EXTENDED_30 -> "Продлен на 30 дней";
            case COMPLETED -> "Завершен успешно";
            case FAILED -> "Не пройден";
            case CANCELLED -> "Отменен";
        };

        String text = String.format("""
            *Статус испытательного срока*
            
            *Период:* %s - %s
            *Длительность:* %d дней
            *Прошло дней:* %d
            *Осталось дней:* %d
            *Статус:* %s
            
            *Статистика отчетов:*
            Отправлено: %d
            Пропущено: %d
            Дней без отчета подряд: %d
            
            *Рекомендации:*
            %s
            """,
                prob.getStartDate(),
                prob.getEndDate(),
                daysTotal,
                daysPassed,
                daysLeft,
                statusMessage,
                reportsSubmitted,
                reportsMissed,
                prob.getDaysWithoutReport(),
                getProbationRecommendation(prob, reportsMissed)
        );

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(text)
                .parseMode("Markdown")
                .build();
    }

    private String getProbationRecommendation(ProbationPeriod prob, int reportsMissed) {
        if (prob.getDaysWithoutReport() >= 2) {
            return "СРОЧНО! Более 2 дней без отчета. Волонтеры свяжутся с вами.";
        } else if (reportsMissed > 3) {
            return "Много пропущенных отчетов. Старайтесь отправлять ежедневно.";
        } else if (prob.getStatus() == ProbationStatus.EXTENDED_14 ||
                prob.getStatus() == ProbationStatus.EXTENDED_30) {
            return "Срок продлен. Уделите больше внимания отчетам.";
        } else {
            return "Продолжайте в том же духе! Отправляйте отчеты ежедневно.";
        }
    }

    // Уведомление волонтеров о новом отчете
    private void notifyVolunteersAboutNewReport(Report report) {
        // Реализация уведомления волонтеров
        // Например, отправка сообщений в чат волонтеров
    }

    // Вызов волонтера
    private SendMessage callVolunteer(User user) {
        user.setCurrentState(BotState.AWAITING_VOLUNTEER);
        userService.saveUser(user);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text("Волонтер уведомлен. Ожидайте ответа.")
                .build();
    }

    // Назад в главное меню
    private SendMessage goBackToMainMenu(User user) {
        StageZeroService stageZero = new StageZeroService();
        return stageZero.showMainMenu(user);
    }
}
