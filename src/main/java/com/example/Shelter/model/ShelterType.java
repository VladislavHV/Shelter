package com.example.Shelter.model;

public enum ShelterType {

    CAT,
    DOG;

    public String getDescription() {
        return switch (this) {
            case CAT -> "Приют для кошек";
            case DOG -> "Приют для собак";
        };
    }

    public String getFullName() {
        return switch (this) {
            case CAT -> "Приют для кошек";
            case DOG -> "Приют для собак";
        };
    }

}
