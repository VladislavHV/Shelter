package com.example.Shelter.controller;

import com.example.Shelter.model.Adoption;
import com.example.Shelter.model.Report;
import com.example.Shelter.service.AdoptionService;
import com.example.Shelter.service.ReportService;
import com.example.Shelter.service.VolunteerNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteer")
@Tag(name = "Волонтеры", description = "API для волонтерских функций")
public class VolunteerController {

    @Autowired
    private AdoptionService adoptionService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private VolunteerNotificationService notificationService;

    @Operation(summary = "Получить заявки на усыновление, требующие внимания")
    @GetMapping("/adoptions/pending")
    public ResponseEntity<List<Adoption>> getPendingAdoptions() {
        List<Adoption> adoptions = adoptionService.getPendingAdoptions();
        return ResponseEntity.ok(adoptions);
    }

    @Operation(summary = "Получить отчеты, требующие проверки")
    @GetMapping("/reports/pending")
    public ResponseEntity<List<Report>> getPendingReports() {
        List<Report> reports = reportService.getPendingReports();
        return ResponseEntity.ok(reports);
    }

    @Operation(summary = "Отправить уведомление волонтерам")
    @PostMapping("/notify")
    public ResponseEntity<String> sendVolunteerNotification(@RequestParam String message) {
        notificationService.sendToVolunteers(message);
        return ResponseEntity.ok("Уведомление отправлено волонтерам");
    }

    @Operation(summary = "Получить статистику")
    @GetMapping("/stats")
    public ResponseEntity<String> getVolunteerStats() {
        long pendingAdoptions = adoptionService.countPendingAdoptions();
        long pendingReports = reportService.countPendingReports();

        String stats = String.format(
                "Статистика для волонтеров:\n" +
                        "• Заявок на усыновление на рассмотрении: %d\n" +
                        "• Отчетов на проверке: %d",
                pendingAdoptions, pendingReports
        );

        return ResponseEntity.ok(stats);
    }
}
