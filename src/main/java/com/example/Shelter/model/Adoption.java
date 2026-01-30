package com.example.Shelter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "adoptions")
public class Adoption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "adoption_date")
    private LocalDateTime adoptionDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "notes", length = 1000)
    private String notes;

    public Adoption() {}

    public Adoption(Long userId, Pet pet) {
        this.userId = userId;
        this.pet = pet;
        this.adoptionDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public LocalDateTime getAdoptionDate() {
        return adoptionDate;
    }

    public void setAdoptionDate(LocalDateTime adoptionDate) {
        this.adoptionDate = adoptionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Adoption{" +
                "id=" + id +
                ", userId=" + userId +
                ", pet=" + (pet != null ? pet.getId() : "null") +
                ", adoptionDate=" + adoptionDate +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
