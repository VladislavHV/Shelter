package com.example.Shelter.repository;

import com.example.Shelter.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
