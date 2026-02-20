package com.example.Shelter.repository;

import com.example.Shelter.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByIsAvailableTrue();

    List<Pet> findByAnimalType(String animalType);

    List<Pet> findByNameContainingIgnoreCase(String name);

    List<Pet> findByBreed(String breed);

    Long countByIsAvailableTrue();
}
