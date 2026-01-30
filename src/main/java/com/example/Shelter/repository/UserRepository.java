package com.example.Shelter.repository;

import com.example.Shelter.model.BotState;
import com.example.Shelter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Находим пользователя по chatId
    Optional<User> findByChatId(Long chatId);

    // Обновляем состояние пользователя
    @Modifying
    @Query("UPDATE User u SET u.botState = :newState WHERE u.chatId = :chatId")
    void updateUserState(@Param("chatId") Long chatId, @Param("newState") BotState newState);

    // Проверяем существование пользователя по chatId
    boolean existsByChatId(Long chatId);
}
