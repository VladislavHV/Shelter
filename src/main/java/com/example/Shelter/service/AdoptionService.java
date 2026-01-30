package com.example.Shelter.service;

import com.example.Shelter.model.Adoption;
import com.example.Shelter.model.Pet;
import com.example.Shelter.exception.AdoptionNotAllowedException;
import com.example.Shelter.exception.ResourceNotFoundException;
import com.example.Shelter.repository.AdoptionRepository;
import com.example.Shelter.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AdoptionService {

    private static final Logger logger = LoggerFactory.getLogger(AdoptionService.class);

    private final AdoptionRepository adoptionRepository;
    private final PetRepository petRepository;

    @Autowired
    public AdoptionService(AdoptionRepository adoptionRepository, PetRepository petRepository) {
        this.adoptionRepository = adoptionRepository;
        this.petRepository = petRepository;
    }

    public Adoption createAdoption(Long userId, Long petId, String notes) {
        try {
            logger.info("Создание заявки на усыновление: userId={}, petId={}", userId, petId);

            // Находим питомца
            Pet pet = petRepository.findById(petId)
                    .orElseThrow(() -> {
                        logger.warn("Питомец не найден: ID {}", petId);
                        return new ResourceNotFoundException("Питомец", "id", petId);
                    });

            // Проверяем доступность
            if (!pet.getIsAvailable()) {
                logger.warn("Питомец {} уже усыновлен или недоступен", petId);
                throw new AdoptionNotAllowedException(pet.getName(), petId);
            }

            // Дополнительные проверки для приюта
            if (pet.getHealthStatus() != null && pet.getHealthStatus().equals("CRITICAL")) {
                throw new AdoptionNotAllowedException(petId, "находится на лечении");
            }

            if (pet.getArrivalDate() != null &&
                    pet.getArrivalDate().isAfter(LocalDate.now().minusDays(14))) {
                throw new AdoptionNotAllowedException(petId, "ещё на карантине (меньше 14 дней в приюте)");
            }

            // Создаем заявку
            Adoption adoption = new Adoption();
            adoption.setUserId(userId);
            adoption.setPet(pet);
            adoption.setAdoptionDate(LocalDateTime.now());
            adoption.setStatus("PENDING");
            adoption.setNotes(notes);

            Adoption savedAdoption = adoptionRepository.save(adoption);
            logger.info("Заявка создана: ID {}", savedAdoption.getId());

            // Обновляем статус питомца
            pet.setIsAvailable(false);
            petRepository.save(pet);
            logger.info("Статус питомца обновлен: недоступен");

            return savedAdoption;

        } catch (Exception e) {
            logger.error("Ошибка при создании заявки", e);
            throw e;
        }
    }

    public List<Adoption> getAllAdoptions() {
        return adoptionRepository.findAll();
    }

    public Adoption getAdoptionById(Long id) {
        return adoptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка", "id", id));
    }

    public Adoption updateAdoptionStatus(Long id, String status) {
        Adoption adoption = getAdoptionById(id);
        adoption.setStatus(status);
        return adoptionRepository.save(adoption);
    }

    public void deleteAdoption(Long id) {
        if (!adoptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Заявка", "id", id);
        }
        adoptionRepository.deleteById(id);
    }

    // Дополнительные методы для приюта
    public List<Adoption> getAdoptionsByUser(Long userId) {
        return adoptionRepository.findByUserId(userId);
    }

    public List<Adoption> getPendingAdoptions() {
        return adoptionRepository.findByStatus("PENDING");
    }

    public List<Adoption> getApprovedAdoptions() {
        return adoptionRepository.findByStatus("APPROVED");
    }
}