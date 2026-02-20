package com.example.Shelter.controller;

import com.example.Shelter.model.Adoption;
import com.example.Shelter.service.AdoptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adoptions")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @GetMapping
    public List<Adoption> getAllAdoptions() {
        return adoptionService.getAllAdoptions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Adoption> getAdoptionById(@PathVariable Long id) {
        Adoption adoption = adoptionService.getAdoptionById(id);
        return adoption != null ? ResponseEntity.ok(adoption) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Adoption createAdoption(@RequestParam Long userId,
                                   @RequestParam Long petId,
                                   @RequestParam(required = false) String notes) {
        return adoptionService.createAdoption(userId, petId, notes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Adoption> updateAdoption(@PathVariable Long id,
                                                   @RequestParam String status) {
        Adoption adoption = adoptionService.updateAdoptionStatus(id, status);
        return adoption != null ? ResponseEntity.ok(adoption) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdoption(@PathVariable Long id) {
        adoptionService.deleteAdoption(id);
        return ResponseEntity.noContent().build();
    }
}
