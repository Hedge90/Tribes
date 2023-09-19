package com.greenfoxacademy.springwebapp.services;

import org.springframework.stereotype.Service;

@Service
public class TimeServiceImpl implements TimeService {
    public Double getTimeDifferenceInMinutesBetweenGivenTimeAndCurrentTime(Long givenTime) {
        Long currentTime = System.currentTimeMillis();
        return (currentTime - givenTime) / 60000.0;
    }

    public Long getTimeDifferenceInMinutesBetweenTwoGivenTime(Long givenTime1, Long givenTime2) {
        return ((givenTime1 - givenTime2) / 1000) / 60;
    }
}
