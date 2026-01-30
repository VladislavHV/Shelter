package com.example.Shelter.service;

import com.example.Shelter.exception.AdoptionNotAllowedException;
import com.example.Shelter.exception.ResourceNotFoundException;
import com.example.Shelter.model.Adoption;
import com.example.Shelter.model.Pet;
import com.example.Shelter.repository.AdoptionRepository;
import com.example.Shelter.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdoptionServiceTest {

    @Mock
    private AdoptionRepository adoptionRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private AdoptionService adoptionService;

    @Test
    void createAdoption_Success() {
        Long userId = 1L;
        Long petId = 1L;
        String notes = "Хочу котика";

        Pet pet = new Pet();
        pet.setId(petId);
        pet.setName("Buddy");
        pet.setIsAvailable(true);

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(adoptionRepository.save(any(Adoption.class))).thenAnswer(invocation -> {
            Adoption adoption = invocation.getArgument(0);
            adoption.setId(100L);
            return adoption;
        });

        Adoption result = adoptionService.createAdoption(userId, petId, notes);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("PENDING", result.getStatus());
        verify(petRepository, times(1)).save(pet);
        assertFalse(pet.getIsAvailable());
    }

    @Test
    void createAdoption_PetNotFound() {
        Long userId = 1L;
        Long petId = 999L;
        String notes = "Заметки";

        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            adoptionService.createAdoption(userId, petId, notes);
        });
    }

    @Test
    void createAdoption_PetNotAvailable() {
        Long userId = 1L;
        Long petId = 1L;
        String notes = "Заметки";

        Pet pet = new Pet();
        pet.setId(petId);
        pet.setIsAvailable(false);

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));

        assertThrows(AdoptionNotAllowedException.class, () -> {
            adoptionService.createAdoption(userId, petId, notes);
        });
    }

    @Test
    void createAdoption_WithEmptyNotes() {
        Long userId = 1L;
        Long petId = 2L;
        String notes = "";

        Pet pet = new Pet();
        pet.setId(petId);
        pet.setName("Мурка");
        pet.setIsAvailable(true);

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(adoptionRepository.save(any(Adoption.class))).thenAnswer(inv -> {
            Adoption a = inv.getArgument(0);
            a.setId(200L);
            return a;
        });

        Adoption result = adoptionService.createAdoption(userId, petId, notes);

        assertNotNull(result);
        assertEquals(200L, result.getId());
    }

}
