package com.example.Shelter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "chosen_shelter")
    private ShelterType chosenShelter;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_state")
    private BotState currentState;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    // Конструктор по умолчанию
    public User() {}

    // Конструктор с параметрами
    public User(Long chatId, String firstName, String lastName,
                String username, ShelterType chosenShelter,
                BotState currentState) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.chosenShelter = chosenShelter;
        this.currentState = currentState;
        this.registrationDate = LocalDateTime.now();
    }

    // Геттеры
    public Long getChatId() {
        return chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public ShelterType getChosenShelter() {
        return chosenShelter;
    }

    public BotState getCurrentState() {
        return currentState;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    // Сеттеры
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setChosenShelter(ShelterType chosenShelter) {
        this.chosenShelter = chosenShelter;
    }

    public void setCurrentState(BotState currentState) {
        this.currentState = currentState;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}