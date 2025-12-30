package com.example.Shelter.service;

import com.example.Shelter.model.ProbationPeriod;
import com.example.Shelter.model.ProbationStatus;
import com.example.Shelter.model.User;
import com.example.Shelter.repository.ProbationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProbationService {

    @Autowired
    private ProbationRepository probationRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private VolunteerNotificationService notificationService;

    public Optional<ProbationPeriod> getActiveProbation(User user) {
        return probationRepository.findByUserAndStatusIn(
                user,
                List.of(ProbationStatus.ACTIVE, ProbationStatus.EXTENDED_14, ProbationStatus.EXTENDED_30)
        );
    }

    // Ежедневная проверка отчетов в 21:00
    @Scheduled(cron = "0 0 21 * * *")
    @Transactional
    public void checkDailyReports() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<ProbationPeriod> activeProbations = probationRepository.findByStatusIn(
                List.of(ProbationStatus.ACTIVE, ProbationStatus.EXTENDED_14, ProbationStatus.EXTENDED_30)
        );

        for (ProbationPeriod probation : activeProbations) {
            boolean hasReport = reportService.hasReportForDate(probation.getUser(), yesterday);

            if (!hasReport) {
                probation.setDaysWithoutReport(probation.getDaysWithoutReport() + 1);

                if (probation.getDaysWithoutReport() == 1) {
                    // Первый день без отчета - напоминание
                    sendReminder(probation.getUser(), "Напоминаем отправить отчет за вчерашний день.");
                } else if (probation.getDaysWithoutReport() >= 2) {
                    // Более 2 дней - уведомление волонтера
                    notificationService.notifyVolunteerAboutMissingReport(probation.getUser(), probation.getDaysWithoutReport());
                }
            } else {
                probation.setDaysWithoutReport(0);
            }

            probationRepository.save(probation);
        }
    }

    // Ежедневная проверка окончания испытательного срока
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void checkProbationEndings() {
        LocalDate today = LocalDate.now();
        List<ProbationPeriod> endingToday = probationRepository.findByEndDate(today);

        for (ProbationPeriod probation : endingToday) {
            // Проверяем количество отчетов
            int totalDays = (int) java.time.temporal.ChronoUnit.DAYS.between(
                    probation.getStartDate(), probation.getEndDate()
            );
            int submittedReports = reportService.getReportsCount(
                    probation.getUser(), probation.getStartDate(), probation.getEndDate().minusDays(1)
            );

            double completionRate = (double) submittedReports / totalDays;

            if (completionRate >= 0.9) {
                // Успешно завершен
                probation.setStatus(ProbationStatus.COMPLETED);
                sendCongratulations(probation.getUser());
            } else if (completionRate >= 0.7) {
                // Продление на 14 дней
                probation.setStatus(ProbationStatus.EXTENDED_14);
                probation.setEndDate(probation.getEndDate().plusDays(14));
                sendExtensionNotice(probation.getUser(), 14);
            } else if (completionRate >= 0.5) {
                // Продление на 30 дней
                probation.setStatus(ProbationStatus.EXTENDED_30);
                probation.setEndDate(probation.getEndDate().plusDays(30));
                sendExtensionNotice(probation.getUser(), 30);
            } else {
                // Не пройден
                probation.setStatus(ProbationStatus.FAILED);
                sendFailureNotice(probation.getUser());
            }

            probationRepository.save(probation);
        }
    }

    private void sendReminder(User user, String message) {
        // Отправка напоминания пользователю
    }

    private void sendCongratulations(User user) {
        // Поздравление с успешным завершением
    }

    private void sendExtensionNotice(User user, int days) {
        // Уведомление о продлении
    }

    private void sendFailureNotice(User user) {
        // Уведомление о неудаче
    }
}