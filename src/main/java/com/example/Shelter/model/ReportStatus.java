package com.example.Shelter.model;

public enum ReportStatus {

    PENDING("Ожидает проверки", "Отправлен, ожидает проверки волонтером"),
    APPROVED("Проверен", "Проверен, все в порядке"),
    NEEDS_IMPROVEMENT("Требует доработки", "Требует доработки, волонтер оставил комментарии"),
    LATE("Опоздал", "Отправлен с опозданием"),
    MISSING("Отсутствует", "Не отправлен");

    private final String displayName;
    private final String description;

    ReportStatus(String displayName, String description) {
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