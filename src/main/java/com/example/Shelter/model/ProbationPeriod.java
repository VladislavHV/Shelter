package com.example.Shelter.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "probation_periods")
public class ProbationPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProbationStatus status;

    @Column(name = "extension_days")
    private Integer extensionDays;

    @Column(name = "days_without_report", nullable = false)
    private Integer daysWithoutReport = 0;

    @Column(name = "total_reports_submitted", nullable = false)
    private Integer totalReportsSubmitted = 0;

    @Column(name = "volunteer_comment", length = 2000)
    private String volunteerComment;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    // Конструкторы
    public ProbationPeriod() {}

    public ProbationPeriod(User user, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = ProbationStatus.ACTIVE;
        this.daysWithoutReport = 0;
        this.totalReportsSubmitted = 0;
        this.createdAt = LocalDate.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public ProbationStatus getStatus() { return status; }
    public void setStatus(ProbationStatus status) {
        this.status = status;
        this.updatedAt = LocalDate.now();
    }

    public Integer getExtensionDays() { return extensionDays; }
    public void setExtensionDays(Integer extensionDays) { this.extensionDays = extensionDays; }

    public Integer getDaysWithoutReport() { return daysWithoutReport; }
    public void setDaysWithoutReport(Integer daysWithoutReport) { this.daysWithoutReport = daysWithoutReport; }

    public Integer getTotalReportsSubmitted() { return totalReportsSubmitted; }
    public void setTotalReportsSubmitted(Integer totalReportsSubmitted) { this.totalReportsSubmitted = totalReportsSubmitted; }

    public String getVolunteerComment() { return volunteerComment; }
    public void setVolunteerComment(String volunteerComment) { this.volunteerComment = volunteerComment; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }

    // Вспомогательные методы
    public boolean isActive() {
        return status == ProbationStatus.ACTIVE ||
                status == ProbationStatus.EXTENDED_14 ||
                status == ProbationStatus.EXTENDED_30;
    }

    public boolean isCompleted() {
        return status == ProbationStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == ProbationStatus.FAILED;
    }

    public int getTotalDays() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }

    public int getDaysPassed() {
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) return 0;
        if (today.isAfter(endDate)) return getTotalDays();
        return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, today);
    }

    public int getDaysLeft() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(endDate)) return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(today, endDate);
    }

    public double getCompletionPercentage() {
        int totalDays = getTotalDays();
        if (totalDays == 0) return 0.0;
        int daysPassed = getDaysPassed();
        return (double) daysPassed / totalDays * 100;
    }

    public void incrementDaysWithoutReport() {
        this.daysWithoutReport++;
        this.updatedAt = LocalDate.now();
    }

    public void resetDaysWithoutReport() {
        this.daysWithoutReport = 0;
        this.updatedAt = LocalDate.now();
    }

    public void incrementReportsSubmitted() {
        this.totalReportsSubmitted++;
        this.updatedAt = LocalDate.now();
    }

    public void extendPeriod(int additionalDays) {
        this.endDate = this.endDate.plusDays(additionalDays);
        this.extensionDays = additionalDays;
        if (additionalDays == 14) {
            this.status = ProbationStatus.EXTENDED_14;
        } else if (additionalDays == 30) {
            this.status = ProbationStatus.EXTENDED_30;
        }
        this.updatedAt = LocalDate.now();
    }
}
