package com.example.Shelter.service;

import com.example.Shelter.model.User;
import com.example.Shelter.model.BotState;
import com.example.Shelter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User getOrCreateUser(Long chatId, org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        Optional<User> existingUser = userRepository.findById(chatId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User newUser = new User();
        newUser.setChatId(chatId);
        newUser.setFirstName(telegramUser.getFirstName());
        newUser.setLastName(telegramUser.getLastName());
        newUser.setUsername(telegramUser.getUserName());
        newUser.setCurrentState(BotState.START);
        newUser.setRegistrationDate(LocalDateTime.now());

        return userRepository.save(newUser);
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUser(Long chatId) {
        return userRepository.findById(chatId);
    }
}
