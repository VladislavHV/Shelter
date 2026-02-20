package com.example.Shelter.controller;

import com.example.Shelter.model.Pet;
import com.example.Shelter.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    // Получить всех питомцев
    @GetMapping("/pets")
    public List<Pet> getAllPets() {
        return menuService.getPets();
    }

    // Получить питомца по ID
    @GetMapping("/pets/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        Pet pet = menuService.getPetById(id);
        return pet != null ? ResponseEntity.ok(pet) : ResponseEntity.notFound().build();
    }

    // Добавить нового питомца
    @PostMapping("/pets")
    public ResponseEntity<Pet> addPet(@RequestBody Pet pet) {
        Pet createdPet = menuService.addPet(pet);
        return ResponseEntity.ok(createdPet);
    }

    // Обновить питомца
    @PutMapping("/pets/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable Long id, @RequestBody Pet pet) {
        pet.setId(id);
        Pet updatedPet = menuService.updatePet(pet);
        return updatedPet != null ? ResponseEntity.ok(updatedPet) : ResponseEntity.notFound().build();
    }

    // Удалить питомца
    @DeleteMapping("/pets/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        menuService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

    // Получить доступных питомцев
    @GetMapping("/pets/available")
    public List<Pet> getAvailablePets() {
        return menuService.getAvailablePets();
    }

    // Получить информацию о приюте
    @GetMapping("/shelter-info")
    public ResponseEntity<String> getShelterInfo(@RequestParam(required = false) String shelterCode) {
        String info = "Информация о приюте";
        if (shelterCode != null) {
            info += " (" + shelterCode + ")";
        }
        return ResponseEntity.ok(info);
    }

    // Позвать волонтера
    @PostMapping("/call-volunteer")
    public ResponseEntity<String> callVolunteer(@RequestParam String message) {
        return ResponseEntity.ok("Волонтер уведомлен: " + message);
    }

    // Получить информацию о пользователе
    @GetMapping("/user/{chatId}")
    public ResponseEntity<String> getUserInfo(@PathVariable Long chatId) {
        return ResponseEntity.ok("Информация о пользователе с chatId: " + chatId);
    }
}