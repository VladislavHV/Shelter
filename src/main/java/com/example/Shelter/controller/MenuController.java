package com.example.Shelter.controller;

import com.example.Shelter.model.Pet;
import com.example.Shelter.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@Tag(name = "Меню", description = "API для работы с меню и питомцами приюта")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Operation(
            summary = "Получить всех питомцев",
            description = "Возвращает список всех питомцев, зарегистрированных в приюте"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список питомцев получен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/pets")
    public List<Pet> getAllPets() {
        return menuService.getPets();
    }

    @Operation(
            summary = "Получить питомца по ID",
            description = "Возвращает информацию о конкретном питомце по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Питомец найден"),
            @ApiResponse(responseCode = "404", description = "Питомец не найден")
    })
    @GetMapping("/pets/{id}")
    public ResponseEntity<Pet> getPetById(
            @Parameter(description = "ID питомца", example = "1", required = true)
            @PathVariable Long id) {
        Pet pet = menuService.getPetById(id);
        return pet != null ? ResponseEntity.ok(pet) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Добавить нового питомца",
            description = "Создает новую запись о питомце в базе данных"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Питомец создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping("/pets")
    public ResponseEntity<Pet> addPet(
            @Parameter(description = "Данные нового питомца", required = true)
            @RequestBody Pet pet) {
        Pet createdPet = menuService.addPet(pet);
        return ResponseEntity.ok(createdPet);
    }

    @Operation(
            summary = "Обновить питомца",
            description = "Обновляет информацию о существующем питомце"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Питомец обновлен"),
            @ApiResponse(responseCode = "404", description = "Питомец не найден")
    })
    @PutMapping("/pets/{id}")
    public ResponseEntity<Pet> updatePet(
            @Parameter(description = "ID питомца", required = true) @PathVariable Long id,
            @Parameter(description = "Обновленные данные питомца", required = true) @RequestBody Pet pet) {
        pet.setId(id);
        Pet updatedPet = menuService.updatePet(pet);
        return updatedPet != null ? ResponseEntity.ok(updatedPet) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Удалить питомца",
            description = "Удаляет питомца из базы данных"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Питомец удален"),
            @ApiResponse(responseCode = "404", description = "Питомец не найден")
    })
    @DeleteMapping("/pets/{id}")
    public ResponseEntity<Void> deletePet(
            @Parameter(description = "ID питомца", required = true) @PathVariable Long id) {
        menuService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Получить доступных питомцев",
            description = "Возвращает список питомцев, доступных для усыновления"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список доступных питомцев получен")
    })
    @GetMapping("/pets/available")
    public List<Pet> getAvailablePets() {
        return menuService.getAvailablePets();
    }

    @Operation(
            summary = "Получить информацию о приюте",
            description = "Возвращает информацию о выбранном приюте (кошачий/собачий)"
    )
    @GetMapping("/shelter-info")
    public ResponseEntity<String> getShelterInfo(
            @Parameter(description = "Код приюта: CAT или DOG", example = "CAT")
            @RequestParam(required = false) String shelterCode) {
        String info = "Информация о приюте";
        if (shelterCode != null) {
            info += " (" + shelterCode + ")";
        }
        return ResponseEntity.ok(info);
    }

    @Operation(
            summary = "Позвать волонтера",
            description = "Отправляет уведомление волонтерам с указанным сообщением"
    )
    @PostMapping("/call-volunteer")
    public ResponseEntity<String> callVolunteer(
            @Parameter(description = "Текст сообщения для волонтера", required = true, example = "Нужна помощь!")
            @RequestParam String message) {
        return ResponseEntity.ok("Волонтер уведомлен: " + message);
    }

    @Operation(
            summary = "Получить информацию о пользователе",
            description = "Возвращает информацию о пользователе по его chatId"
    )
    @GetMapping("/user/{chatId}")
    public ResponseEntity<String> getUserInfo(
            @Parameter(description = "Chat ID пользователя в Telegram", required = true, example = "123456789")
            @PathVariable Long chatId) {
        return ResponseEntity.ok("Информация о пользователе с chatId: " + chatId);
    }
}