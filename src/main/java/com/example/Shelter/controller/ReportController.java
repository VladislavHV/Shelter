package com.example.Shelter.controller;

import com.example.Shelter.model.Report;
import com.example.Shelter.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@Tag(name = "Отчеты", description = "Управление отчетами о питомцах")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Operation(summary = "Получить все отчеты")
    @GetMapping
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @Operation(summary = "Получить отчет по ID")
    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        return report != null ? ResponseEntity.ok(report) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Создать новый отчет")
    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return reportService.createReport(report);
    }

    @Operation(summary = "Подтвердить отчет")
    @PutMapping("/{id}/approve")
    public ResponseEntity<Report> approveReport(@PathVariable Long id) {
        Report report = reportService.approveReport(id);
        return report != null ? ResponseEntity.ok(report) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Отправить отчет на доработку")
    @PostMapping("/{id}/needs-improvement")
    public ResponseEntity<Report> rejectReport(
            @Parameter(description = "ID отчета") @PathVariable Long id,
            @Parameter(description = "Причина доработки") @RequestParam(required = false) String rejectionReason) {
        Report report = reportService.rejectReport(id, rejectionReason);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Получить отчеты, ожидающие проверки")
    @GetMapping("/pending")
    public List<Report> getPendingReports() {
        return reportService.getPendingReports();
    }
}
