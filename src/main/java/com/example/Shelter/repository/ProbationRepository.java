package com.example.Shelter.repository;

import com.example.Shelter.model.ProbationPeriod;
import com.example.Shelter.model.ProbationStatus;
import com.example.Shelter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProbationRepository extends JpaRepository<ProbationPeriod, Long> {
    Optional<ProbationPeriod> findByUser(User user);
    List<ProbationPeriod> findByStatus(ProbationStatus status);
    List<ProbationPeriod> findByStatusIn(List<ProbationStatus> statuses);
    List<ProbationPeriod> findByEndDate(LocalDate endDate);
    List<ProbationPeriod> findByEndDateBefore(LocalDate date);

    @Query("SELECT p FROM ProbationPeriod p WHERE p.status IN :statuses AND p.endDate = :today")
    List<ProbationPeriod> findActiveProbationsEndingToday(
            @Param("statuses") List<ProbationStatus> statuses,
            @Param("today") LocalDate today);

    boolean existsByUser(User user);

    @Query("SELECT p FROM ProbationPeriod p WHERE p.user = :user AND p.status IN ('ACTIVE', 'EXTENDED_14', 'EXTENDED_30')")
    Optional<ProbationPeriod> findActiveProbationByUser(@Param("user") User user);

    @Query("SELECT p FROM ProbationPeriod p WHERE p.user = :user AND p.status IN :statuses")
    Optional<ProbationPeriod> findByUserAndStatusIn(
            @Param("user") User user,
            @Param("statuses") List<ProbationStatus> statuses);
}
