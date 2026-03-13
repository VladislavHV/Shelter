# Shelter Bot API

**Telegram бот для приюта животных**  
REST API для управления питомцами, заявками на усыновление, отчетами и пользователями.

---

## Технологии

- Java 17
- Spring Boot 3.5.6
- Spring Data JPA
- Spring Web
- PostgreSQL / H2 (база данных)
- Swagger / OpenAPI 3.0 (springdoc-openapi)
- Maven
- Telegram Bot API

---

## Установка и запуск

### 1. Клонирование репозитория

```bash
git clone https://github.com/VladislavHV/Shelter.git
cd Shelter
```

### 2. Настройка базы данных

```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/shelter_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Сборка и запуск

./mvnw clean install
./mvnw spring-boot:run

## API документация (Swagger)

http://localhost:8080/swagger-ui/index.html

## Структура проекта

com.example.Shelter/
├── controller/          # REST контроллеры
├── service/             # Бизнес-логика
├── repository/          # Работа с БД
├── model/               # Сущности (Pet, User, Adoption, Report)
├── exception/           # Обработка ошибок
├── config/              # Конфигурации (Swagger)
├── bot/                 # Бот
└── ShelterApplication.java

## Тестирование API

curl -X GET http://localhost:8080/pets
Swagger UI — http://localhost:8080/swagger-ui/index.html

## Основные эндпоинты

Метод	URL	Описание
GET	/pets	Все питомцы
GET	/pets/{id}	Питомец по ID
POST	/pets	Добавить питомца
PUT	/pets/{id}	Обновить питомца
DELETE	/pets/{id}	Удалить питомца
GET	/pets/available	Доступные питомцы
GET	/adoptions	Все заявки
POST	/adoptions	Создать заявку
GET	/users	Все пользователи
POST	/reports	Создать отчет
GET	/volunteer/stats	Статистика для волонтера
GET	/health	Проверка состояния приложения

## Сборка и зависимости

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.6</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.8.6</version>
    </dependency>
    <dependency>
        <groupId>org.telegram</groupId>
        <artifactId>telegrambots-spring-boot-starter</artifactId>
        <version>6.9.7.1</version>
    </dependency>
</dependencies>

## Разработка

# Требования
Java 17+
Maven 3.8+
PostgreSQL (опционально)

# Рекомендации
Перед коммитом форматируй код
Используй осмысленные названия коммитов
Добавляй JavaDoc для новых методов
Тестируй через Swagger перед отправкой
