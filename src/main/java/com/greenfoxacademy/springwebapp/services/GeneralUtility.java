package com.greenfoxacademy.springwebapp.services;

public final class GeneralUtility {

    public static boolean isEmptyOrNull(String data) {
        return (data == null || data.isBlank());
    }

    public static int generateRandomNumber(int maxNumber) {
        return (int) (Math.random() * maxNumber) + 1;
    }

    public static boolean hasLessCharactersThan(String data, int length) {
        return (data.length() >= length);
    }
}
