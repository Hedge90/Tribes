package com.greenfoxacademy.springwebapp.dtos;

public class KingdomNameDTO {

    private String name;


    public KingdomNameDTO() {
    }

    public KingdomNameDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

