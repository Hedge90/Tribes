package com.greenfoxacademy.springwebapp.services;

public interface TimeService {
    Double getTimeDifferenceInMinutesBetweenGivenTimeAndCurrentTime(Long givenTime);

    Long getTimeDifferenceInMinutesBetweenTwoGivenTime(Long givenTime1, Long givenTime2);
}