package com.greenfoxacademy.springwebapp.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GeneralUtilityTest {

    @Test
    void isEmptyOrNull_WithValidData_ReturnsFalse() {
        Assertions.assertFalse(GeneralUtility.isEmptyOrNull("data"));
    }

    @Test
    void isEmptyOrNull_WithEmptyData_ReturnsTrue() {
        Assertions.assertTrue(GeneralUtility.isEmptyOrNull(""));
    }

    @Test
    void isEmptyOrNull_WithNull_ReturnsTrue() {
        Assertions.assertTrue(GeneralUtility.isEmptyOrNull(null));
    }

    @Test
    void generateRandomNumber_CheckingWhenTheReturnValueIsInRange_ReturnsTrue() {
        int number = GeneralUtility.generateRandomNumber(10);
        Assertions.assertTrue((0 < number && number <= 10));
    }

    @Test
    void isLongEnough_WithValidDataAndLength_ReturnsTrue() {
        Assertions.assertTrue(GeneralUtility.hasLessCharactersThan("data", 3));
    }

    @Test
    void isLongEnough_WithValidDataAndBiggerLength_ReturnsFalse() {
        Assertions.assertFalse(GeneralUtility.hasLessCharactersThan("data", 10));
    }
}
