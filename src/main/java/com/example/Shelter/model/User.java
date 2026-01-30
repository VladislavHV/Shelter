package com.example.Shelter.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id", unique = true, nullable = false)
    private Long telegramId;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_name")
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "bot_state")
    private BotState botState = BotState.STAGE_ZERO;

    @Column(name = "selected_shelter")
    private String selectedShelter; // "CAT", "DOG"

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "has_active_adoption")
    private Boolean hasActiveAdoption = false;

    @Column(name = "current_pet_id")
    private Long currentPetId;

    // ВАЖНО: конструктор по умолчанию
    public User() {
    }

    // Опционально: конструктор со всеми полями
    public User(Long id, Long telegramId, Long chatId, String firstName,
                String lastName, String userName, BotState botState,
                String selectedShelter, String phoneNumber, String email,
                Boolean hasActiveAdoption, Long currentPetId) {
        this.id = id;
        this.telegramId = telegramId;
        this.chatId = chatId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.botState = botState;
        this.selectedShelter = selectedShelter;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.hasActiveAdoption = hasActiveAdoption;
        this.currentPetId = currentPetId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }

    public String getSelectedShelter() {
        return selectedShelter;
    }

    public void setSelectedShelter(String selectedShelter) {
        this.selectedShelter = selectedShelter;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getHasActiveAdoption() {
        return hasActiveAdoption;
    }

    public void setHasActiveAdoption(Boolean hasActiveAdoption) {
        this.hasActiveAdoption = hasActiveAdoption;
    }

    public Long getCurrentPetId() {
        return currentPetId;
    }

    public void setCurrentPetId(Long currentPetId) {
        this.currentPetId = currentPetId;
    }
}