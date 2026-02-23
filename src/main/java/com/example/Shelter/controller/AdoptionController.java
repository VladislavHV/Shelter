package com.example.Shelter.controller;

import com.example.Shelter.model.Adoption;
import com.example.Shelter.service.AdoptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adoptions")
@Tag(name = "Усыновления", description = "Управление заявками на усыновление")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @Operation(summary = "Получить все заявки")
    @GetMapping
    public List<Adoption> getAllAdoptions() {
        return adoptionService.getAllAdoptions();
    }

    @Operation(summary = "Получить заявку по ID")
    @GetMapping("/{id}")
    public ResponseEntity<Adoption> getAdoptionById(@PathVariable Long id) {
        Adoption adoption = adoptionService.getAdoptionById(id);
        return adoption != null ? ResponseEntity.ok(adoption) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Создать новую заявку")
    @PostMapping
    public Adoption createAdoption(
            @Parameter(description = "ID пользователя") @RequestParam Long userId,
            @Parameter(description = "ID питомца") @RequestParam Long petId,
            @Parameter(description = "Примечания") @RequestParam(required = false) String notes) {
        return adoptionService.createAdoption(userId, petId, notes);
    }

    @Operation(summary = "Обновить статус заявки")
    @PutMapping("/{id}")
    public ResponseEntity<Adoption> updateAdoption(
            @Parameter(description = "ID заявки") @PathVariable Long id,
            @Parameter(description = "Новый статус") @RequestParam String status) {
        Adoption adoption = adoptionService.updateAdoptionStatus(id, status);
        return adoption != null ? ResponseEntity.ok(adoption) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Удалить заявку")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdoption(@PathVariable Long id) {
        adoptionService.deleteAdoption(id);
        return ResponseEntity.noContent().build();
    }
}
