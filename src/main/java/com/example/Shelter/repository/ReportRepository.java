package com.example.Shelter.repository;

import com.example.Shelter.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByUserAndReportDate(User user, LocalDate reportDate);
    boolean existsByUserAndReportDate(User user, LocalDate reportDate);
    List<Report> findByUserOrderByReportDateDesc(User user);
    List<Report> findByStatus(ReportStatus status);
    int countByUserAndReportDateBetween(User user, LocalDate startDate, LocalDate endDate);

    // Отчеты за период
    @Query("SELECT r FROM Report r WHERE r.user = :user AND r.reportDate BETWEEN :startDate AND :endDate")
    List<Report> findByUserAndReportDateBetween(@Param("user") User user,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    // Просроченные отчеты (старше 1 дня)
    @Query("SELECT r FROM Report r WHERE r.status = 'PENDING' AND r.reportDate < :date")
    List<Report> findPendingReportsOlderThan(@Param("date") LocalDate date);

    // Отчеты без фото
    @Query("SELECT r FROM Report r WHERE r.photoPath IS NULL OR r.photoPath = ''")
    List<Report> findReportsWithoutPhoto();

    // Отчеты без текстового описания
    @Query("SELECT r FROM Report r WHERE r.diet IS NULL OR r.diet = '' OR r.wellbeing IS NULL OR r.wellbeing = '' OR r.behaviorChanges IS NULL OR r.behaviorChanges = ''")
    List<Report> findIncompleteReports();

    List<Report> findAll();

    long countByStatus(ReportStatus status);
}
