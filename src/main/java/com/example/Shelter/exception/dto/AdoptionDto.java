package com.example.Shelter.exception.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;

public class AdoptionDto {
    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Pet ID is required")
    private Long petId;

    private LocalDateTime adoptionDate;
    private String status;
    private String notes;

    public AdoptionDto() {}

    public AdoptionDto(Long userId, Long petId) {
        this.userId = userId;
        this.petId = petId;
        this.adoptionDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getPetId() {
        return petId;
    }

    public LocalDateTime getAdoptionDate() {
        return adoptionDate;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public void setAdoptionDate(LocalDateTime adoptionDate) {
        this.adoptionDate = adoptionDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "AdoptionDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", petId=" + petId +
                ", adoptionDate=" + adoptionDate +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
