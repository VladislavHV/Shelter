package com.example.Shelter.model;

public enum ProbationStatus {

    ACTIVE("Активен", "Испытательный срок идет"),
    COMPLETED("Завершен", "Успешно завершен"),
    EXTENDED_14("Продлен на 14 дней", "Испытательный срок продлен на 14 дней"),
    EXTENDED_30("Продлен на 30 дней", "Испытательный срок продлен на 30 дней"),
    FAILED("Не пройден", "Испытательный срок не пройден"),
    CANCELLED("Отменен", "Испытательный срок отменен");

    private final String displayName;
    private final String description;

    ProbationStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}