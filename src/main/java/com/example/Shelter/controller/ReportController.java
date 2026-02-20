package com.example.Shelter.controller;

import com.example.Shelter.model.Report;
import com.example.Shelter.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        return report != null ? ResponseEntity.ok(report) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return reportService.createReport(report);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Report> approveReport(@PathVariable Long id) {
        Report report = reportService.approveReport(id);
        return report != null ? ResponseEntity.ok(report) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Отклонить отчет (требует доработки)")
    @PostMapping("/{id}/needs-improvement")
    public ResponseEntity<Report> rejectReport(
            @PathVariable Long id,
            @RequestParam(required = false) String rejectionReason) {

        Report report = reportService.rejectReport(id, rejectionReason);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/pending")
    public List<Report> getPendingReports() {
        return reportService.getPendingReports();
    }
}
