package com.example.Shelter.service;

import com.example.Shelter.model.BotState;
import com.example.Shelter.model.User;
import com.example.Shelter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User getOrCreateUser(Long chatId, org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        return userRepository.findByChatId(chatId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setChatId(chatId);
                    newUser.setTelegramId(telegramUser.getId());
                    newUser.setFirstName(telegramUser.getFirstName());
                    newUser.setLastName(telegramUser.getLastName());
                    newUser.setUserName(telegramUser.getUserName());
                    newUser.setBotState(BotState.STAGE_ZERO);
                    return userRepository.save(newUser);
                });
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUser(Long chatId) {
        return userRepository.findById(chatId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User updateUser(User user) {
        if (user.getId() == null || !userRepository.existsById(user.getId())) {
            return null; // или выбросить исключение
        }
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User findByChatId(Long chatId) {
        return userRepository.findByChatId(chatId).orElse(null);
    }

    public User findByTelegramId(Integer telegramId) {
        return userRepository.findByTelegramId(telegramId).orElse(null);
    }

    @Transactional
    public User updateUserState(Long id, BotState botState) {
        User user = getUserById(id);
        if (user != null) {
            user.setBotState(botState);
            return userRepository.save(user);
        }
        return null;
    }

    public List<User> getUsersByBotState(BotState botState) {
        return userRepository.findByBotState(botState);
    }
}
