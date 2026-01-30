package com.example.Shelter.exception;

public class AdoptionNotAllowedException extends RuntimeException {

    // Конструктор с сообщением
    public AdoptionNotAllowedException(String message) {
        super(message);
    }

    // Конструктор с именем и ID питомца
    public AdoptionNotAllowedException(String petName, Long petId) {
        super(String.format("Животное '%s' (ID: %d) недоступно для усыновления", petName, petId));
    }

    // Дополнительный конструктор для приюта
    public AdoptionNotAllowedException(Long petId, String reason) {
        super(String.format("Питомец ID %d: %s", petId, reason));
    }
}
