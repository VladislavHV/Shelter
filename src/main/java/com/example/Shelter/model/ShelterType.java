package com.example.Shelter.model;

public enum ShelterType {

    CAT_SHELTER,
    DOG_SHELTER;

    public String getDescription() {
        return switch (this) {
            case CAT_SHELTER -> "Приют для кошек";
            case DOG_SHELTER -> "Приют для собак";
        };
    }

    public String getFullName() {
        return switch (this) {
            case CAT_SHELTER -> "Приют для кошек";
            case DOG_SHELTER -> "Приют для собак";
        };
    }

}
