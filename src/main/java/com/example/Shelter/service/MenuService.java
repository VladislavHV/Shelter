package com.example.Shelter.service;

import com.example.Shelter.model.Pet;
import com.example.Shelter.model.User;
import com.example.Shelter.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private PetRepository petRepository;

    public SendMessage showMainMenu(User user) {
        String shelterCode = user.getSelectedShelter();
        String shelterName = getShelterDescription(shelterCode);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Информация о приюте");
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Как взять животное");
        keyboard.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Отчет о питомце");
        keyboard.add(row3);

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Позвать волонтера");
        keyboard.add(row4);

        ReplyKeyboardMarkup replyMarkup = ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .build();

        String messageText = String.format("Вы выбрали: %s\n\nЧто вас интересует?", shelterName);

        return SendMessage.builder()
                .chatId(user.getChatId())
                .text(messageText)
                .replyMarkup(replyMarkup)
                .build();
    }

    private String getShelterDescription(String shelterCode) {
        if (shelterCode == null) {
            return "приют";
        }
        switch (shelterCode) {
            case "CAT":
                return "кошачий приют";
            case "DOG":
                return "собачий приют";
            default:
                return "приют";
        }
    }

    // Получить всех питомцев
    public List<Pet> getPets() {
        return petRepository.findAll();
    }

    // Получить питомца по ID
    public Pet getPetById(Long id) {
        return petRepository.findById(id).orElse(null);
    }

    // Добавить нового питомца
    public Pet addPet(Pet pet) {
        if (pet.getIsAvailable() == null) {
            pet.setIsAvailable(true);
        }
        if (pet.getIsAvailable() == null) {
            pet.setIsAvailable(true);
        }
        if (pet.getArrivalDate() == null) {
            pet.setArrivalDate(LocalDate.now());
        }
        return petRepository.save(pet);
    }

    // Обновить питомца
    public Pet updatePet(Pet pet) {
        if (pet.getId() == null || !petRepository.existsById(pet.getId())) {
            return null;
        }
        return petRepository.save(pet);
    }

    // Удалить питомца
    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }

    // Получить доступных питомцев
    public List<Pet> getAvailablePets() {
        return petRepository.findByIsAvailableTrue();
    }

}
