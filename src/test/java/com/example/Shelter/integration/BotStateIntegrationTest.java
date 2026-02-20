package com.example.Shelter.integration;

import com.example.Shelter.bot.handlers.MessageHandler;
import com.example.Shelter.config.TestBotConfig;
import com.example.Shelter.model.BotState;
import com.example.Shelter.model.User;
import com.example.Shelter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestBotConfig.class)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BotStateIntegrationTest {

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private UserRepository userRepository;

    private static final Long TEST_CHAT_ID = 12345L;
    private static final Long TEST_USER_ID = 67890L;

    @BeforeEach
    void setUp() {
        userRepository.findByChatId(TEST_CHAT_ID)
                .ifPresent(user -> userRepository.delete(user));
    }

    @Test
    void testUserCreationWithoutTelegram() {
        // Простой тест без интеграции с Telegram
        User user = new User();
        user.setChatId(TEST_CHAT_ID);
        user.setTelegramId(TEST_USER_ID);
        user.setBotState(BotState.STAGE_ZERO);
        user.setFirstName("Test");
        user.setUserName("testuser");

        userRepository.save(user);

        // Проверяем сохранение
        Optional<User> savedUser = userRepository.findByChatId(TEST_CHAT_ID);
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getBotState()).isEqualTo(BotState.STAGE_ZERO);
        assertThat(savedUser.get().getUserName()).isEqualTo("testuser");
    }

    @Test
    void testUserStateTransitions() {
        User user = new User();
        user.setChatId(TEST_CHAT_ID);
        user.setTelegramId(TEST_USER_ID);
        user.setBotState(BotState.STAGE_ZERO);
        userRepository.save(user);

        User savedUser = userRepository.findByChatId(TEST_CHAT_ID).orElseThrow();
        savedUser.setSelectedShelter("CAT");
        savedUser.setBotState(BotState.MAIN_MENU);
        userRepository.save(savedUser);

        Optional<User> updatedUser = userRepository.findByChatId(TEST_CHAT_ID);
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getBotState()).isEqualTo(BotState.MAIN_MENU);
        assertThat(updatedUser.get().getSelectedShelter()).isEqualTo("CAT");

        updatedUser.get().setBotState(BotState.SHELTER_INFO_MENU);
        userRepository.save(updatedUser.get());

        Optional<User> finalUser = userRepository.findByChatId(TEST_CHAT_ID);
        assertThat(finalUser).isPresent();
        assertThat(finalUser.get().getBotState()).isEqualTo(BotState.SHELTER_INFO_MENU);
    }

    @Test
    void testMultipleUsers() {
        // Тестируем независимость пользователей

        User user1 = new User();
        user1.setChatId(11111L);
        user1.setTelegramId(22222L);
        user1.setBotState(BotState.STAGE_ZERO);
        userRepository.save(user1);

        User user2 = new User();
        user2.setChatId(33333L);
        user2.setTelegramId(44444L);
        user2.setBotState(BotState.MAIN_MENU);
        user2.setSelectedShelter("DOG");
        userRepository.save(user2);

        Optional<User> found1 = userRepository.findByChatId(11111L);
        Optional<User> found2 = userRepository.findByChatId(33333L);

        assertThat(found1).isPresent();
        assertThat(found2).isPresent();

        assertThat(found1.get().getBotState()).isEqualTo(BotState.STAGE_ZERO);
        assertThat(found2.get().getBotState()).isEqualTo(BotState.MAIN_MENU);

        assertThat(found1.get().getSelectedShelter()).isNull();
        assertThat(found2.get().getSelectedShelter()).isEqualTo("DOG");
    }
}