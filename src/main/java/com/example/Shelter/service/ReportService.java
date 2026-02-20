package com.example.Shelter.service;

import com.example.Shelter.model.Report;
import com.example.Shelter.model.ReportStatus;
import com.example.Shelter.model.User;
import com.example.Shelter.repository.ReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private VolunteerNotificationService notificationService;

    public Optional<Report> getReportForDate(User user, LocalDate date) {
        return reportRepository.findByUserAndReportDate(user, date);
    }

    public Report getOrCreateReport(User user, LocalDate date) {
        return reportRepository.findByUserAndReportDate(user, date)
                .orElseGet(() -> {
                    Report report = new Report();
                    report.setUser(user);
                    report.setReportDate(date);
                    report.setStatus(ReportStatus.PENDING);
                    return reportRepository.save(report);
                });
    }

    public Optional<Report> findById(Long id) {
        return reportRepository.findById(id);
    }

    public Optional<Report> findByUserAndDate(User user, LocalDate date) {
        return reportRepository.findByUserAndReportDate(user, date);
    }

    public boolean existsByUserAndDate(User user, LocalDate date) {
        return reportRepository.existsByUserAndReportDate(user, date);
    }

    public List<Report> findAllByUser(User user) {
        return reportRepository.findByUserOrderByReportDateDesc(user);
    }

    public List<Report> findPendingReports() {
        return reportRepository.findByStatus(ReportStatus.PENDING);
    }

    public void saveReport(Report report) {
        reportRepository.save(report);
    }

    public boolean hasReportForDate(User user, LocalDate date) {
        return reportRepository.existsByUserAndReportDate(user, date);
    }

    public List<Report> getUserReports(User user) {
        return reportRepository.findByUserOrderByReportDateDesc(user);
    }

    public int getReportsCount(User user, LocalDate startDate, LocalDate endDate) {
        return reportRepository.countByUserAndReportDateBetween(user, startDate, endDate);
    }

    public List<Report> getPendingReports() {
        return reportRepository.findByStatus(ReportStatus.PENDING);
    }

    @Transactional
    public Report createTestReport(User user) {
        Report report = new Report();
        report.setUser(user);
        report.setReportDate(LocalDate.now());
        report.setDiet("Тестовый рацион: корм Royal Canin 100г");
        report.setWellbeing("Самочувствие отличное, активный");
        report.setBehaviorChanges("Научился команде 'сидеть'");
        report.setStatus(ReportStatus.PENDING);
        report.setCreatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }

    // Создание нового отчета
    @Transactional
    public Report createDailyReport(User user, LocalDate date,
                                    String photoPath, String diet,
                                    String wellbeing, String behaviorChanges) {

        Report report = new Report();
        report.setUser(user);
        report.setReportDate(date);
        report.setPhotoPath(photoPath);
        report.setDiet(diet);
        report.setWellbeing(wellbeing);
        report.setBehaviorChanges(behaviorChanges);
        report.setStatus(ReportStatus.PENDING);
        report.setCreatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }

    // Обновление отчета
    @Transactional
    public Report updateReport(Long reportId, String photoPath, String diet,
                               String wellbeing, String behaviorChanges) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (photoPath != null) report.setPhotoPath(photoPath);
        if (diet != null) report.setDiet(diet);
        if (wellbeing != null) report.setWellbeing(wellbeing);
        if (behaviorChanges != null) report.setBehaviorChanges(behaviorChanges);

        return reportRepository.save(report);
    }

    // Валидация и проверка отчетов
    public boolean validateReport(Report report) {
        if (report == null) return false;

        boolean hasPhoto = report.getPhotoPath() != null && !report.getPhotoPath().isEmpty();
        boolean hasDiet = report.getDiet() != null && report.getDiet().length() >= 10;
        boolean hasWellbeing = report.getWellbeing() != null && report.getWellbeing().length() >= 10;
        boolean hasBehavior = report.getBehaviorChanges() != null && report.getBehaviorChanges().length() >= 10;

        return hasPhoto && hasDiet && hasWellbeing && hasBehavior;
    }

    // Волонтер проверяет отчет
    @Transactional
    public Report reviewReport(Long reportId, ReportStatus status, String feedback) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(status);
        report.setVolunteerFeedback(feedback);

        return reportRepository.save(report);
    }

    // Автоматическая проверка в 21:00
    @Scheduled(cron = "0 0 21 * * *")
    @Transactional
    public void checkDailyReports() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Report> pendingReports = reportRepository.findByStatus(ReportStatus.PENDING);

        for (Report report : pendingReports) {
            if (report.getReportDate().isBefore(yesterday)) {
                // Отчет просрочен
                report.setStatus(ReportStatus.LATE);
                reportRepository.save(report);

                // Уведомляем волонтера
                notificationService.notifyVolunteerAboutBadReport(
                        report.getUser(),
                        report.getReportDate().toString(),
                        "Отчет не проверен более суток"
                );
            }
        }
    }

    // Статистика
    public ReportStatistics getStatistics(User user, LocalDate startDate, LocalDate endDate) {
        int total = reportRepository.countByUserAndReportDateBetween(user, startDate, endDate);
        int pending = (int) reportRepository.findByUserAndReportDateBetween(user, startDate, endDate)
                .stream()
                .filter(r -> r.getStatus() == ReportStatus.PENDING)
                .count();
        int approved = (int) reportRepository.findByUserAndReportDateBetween(user, startDate, endDate)
                .stream()
                .filter(r -> r.getStatus() == ReportStatus.APPROVED)
                .count();

        return new ReportStatistics(total, pending, approved);
    }

    // Внутренний класс для статистики
    public static class ReportStatistics {
        private final int totalReports;
        private final int pendingReports;
        private final int approvedReports;

        public ReportStatistics(int totalReports, int pendingReports, int approvedReports) {
            this.totalReports = totalReports;
            this.pendingReports = pendingReports;
            this.approvedReports = approvedReports;
        }

        public int getTotalReports() { return totalReports; }
        public int getPendingReports() { return pendingReports; }
        public int getApprovedReports() { return approvedReports; }

        public double getApprovalRate() {
            return totalReports > 0 ? (double) approvedReports / totalReports * 100 : 0;
        }
    }

    // Получить все отчеты
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    // Получить отчет по ID
    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
    }

    // Создать отчет
    public Report createReport(Report report) {
        return reportRepository.save(report);
    }

    // Обновить отчет
    public Report updateReport(Long id, Report reportDetails) {
        Report report = getReportById(id);

        if (reportDetails.getPhotoPath() != null)
            report.setPhotoPath(reportDetails.getPhotoPath());
        if (reportDetails.getDiet() != null)
            report.setDiet(reportDetails.getDiet());
        if (reportDetails.getWellbeing() != null)
            report.setWellbeing(reportDetails.getWellbeing());
        if (reportDetails.getBehaviorChanges() != null)
            report.setBehaviorChanges(reportDetails.getBehaviorChanges());
        if (reportDetails.getStatus() != null)
            report.setStatus(reportDetails.getStatus());

        return reportRepository.save(report);
    }

    // Удалить отчет
    public void deleteReport(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new RuntimeException("Report not found with id: " + id);
        }
        reportRepository.deleteById(id);
    }

    // Получить отчеты по ID пользователя
    public List<Report> getReportsByUserId(Long userId) {
        return List.of();
    }

    // Получить отчеты по ID животного
    public List<Report> getReportsByPetId(Long petId) {
        return List.of();
    }

    // Количество отчетов со статусом PENDING
    public long countPendingReports() {
        return reportRepository.countByStatus(ReportStatus.PENDING);
    }

    // Подтвердить отчет
    public Report approveReport(Long id) {
        Report report = getReportById(id);
        report.setStatus(ReportStatus.APPROVED);
        return reportRepository.save(report);
    }

    // Отклонить отчет
    public Report rejectReport(Long id, String rejectionReason) {
        Report report = getReportById(id);
        report.setStatus(ReportStatus.NEEDS_IMPROVEMENT);
        if (rejectionReason != null) {
            report.setVolunteerFeedback(rejectionReason);
        }
        return reportRepository.save(report);
    }

    // Получить отчеты по статусу
    public List<Report> getReportsByStatus(String status) {
        try {
            ReportStatus reportStatus = ReportStatus.valueOf(status.toUpperCase());
            return reportRepository.findByStatus(reportStatus);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

}
