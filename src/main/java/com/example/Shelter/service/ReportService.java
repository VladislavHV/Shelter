package com.example.Shelter.service;

import com.example.Shelter.model.Report;
import com.example.Shelter.model.ReportStatus;
import com.example.Shelter.model.User;
import com.example.Shelter.repository.ProbationRepository;
import com.example.Shelter.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ProbationRepository probationRepository;

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
}
