package com.example.Shelter.service;

import com.example.Shelter.model.User;
import org.springframework.stereotype.Service;

@Service
public class VolunteerNotificationService {

    // Уведомление волонтера о пропущенном отчете
    public void notifyVolunteerAboutMissingReport(User user, int daysWithoutReport) {
        String message = String.format("""
            *Усыновитель пропускает отчеты!*
            
            Пользователь: %s %s (@%s)
            Chat ID: %d
            Дней без отчета: %d
            
            Необходимо связаться с усыновителем.
            """,
                user.getFirstName(),
                user.getLastName(),
                user.getUserName(),
                user.getChatId(),
                daysWithoutReport
        );

        System.out.println("[VOLUNTEER NOTIFICATION] " + message);
        sendToVolunteerChat(message);
    }

    // Уведомление волонтера о проблемах с отчетом
    public void notifyVolunteerAboutBadReport(User user, String reportDate, String issues) {
        String message = String.format("""
            *Проблемы с отчетом*
            
            Пользователь: %s %s
            Дата отчета: %s
            Проблемы: %s
            
            Требуется обратная связь.
            """,
                user.getFirstName(),
                user.getLastName(),
                reportDate,
                issues
        );

        sendToVolunteerChat(message);
    }

    // Уведомление о завершении испытательного срока
    public void notifyVolunteerAboutProbationEnd(User user, String result) {
        String message = String.format("""
            *Испытательный срок завершен*
            
            Пользователь: %s %s
            Результат: %s
            
            Требуется принять решение.
            """,
                user.getFirstName(),
                user.getLastName(),
                result
        );

        sendToVolunteerChat(message);
    }

    // Отправка напоминания пользователю
    public void sendReminderToUser(User user, String reminderText) {
        System.out.println("[REMINDER TO USER] ChatId: " + user.getChatId() + " - " + reminderText);
    }

    // Уведомление о продлении испытательного срока
    public void notifyProbationExtended(User user, int additionalDays, java.time.LocalDate newEndDate) {
        String message = String.format("""
            *Испытательный срок продлен*
            
            Пользователь: %s %s
            Дополнительных дней: %d
            Новый срок окончания: %s
            """,
                user.getFirstName(),
                user.getLastName(),
                additionalDays,
                newEndDate
        );

        sendToVolunteerChat(message);
    }

    // Уведомление о неудачном завершении испытательного срока
    public void notifyProbationFailed(User user) {
        String message = String.format("""
            *Испытательный срок не пройден*
            
            Пользователь: %s %s
            Chat ID: %d
            
            Требуется изъятие животного.
            """,
                user.getFirstName(),
                user.getLastName(),
                user.getChatId()
        );

        sendToVolunteerChat(message);
    }

    // Уведомление об успешном завершении испытательного срока
    public void notifyProbationCompleted(User user) {
        String message = String.format("""
            *Испытательный срок успешно завершен!*
            
            Пользователь: %s %s
            Chat ID: %d
            
            Животное остается у усыновителя.
            """,
                user.getFirstName(),
                user.getLastName(),
                user.getChatId()
        );

        sendToVolunteerChat(message);
    }

    // Приватный метод для отправки в чат волонтеров
    private void sendToVolunteerChat(String message) {
        System.out.println("[TO VOLUNTEER CHAT]: " + message);
    }

}
