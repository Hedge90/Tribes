package com.greenfoxacademy.springwebapp.dtos;

public class RegisteredUserDTO {
    private Long id;
    private String username;
    private String email;
    private Long kingdomId;
    private int points;

    public RegisteredUserDTO() {
    }

    public RegisteredUserDTO(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getKingdomId() {
        return kingdomId;
    }

    public void setKingdomId(Long kingdomId) {
        this.kingdomId = kingdomId;
    }

    public int getPoints() {
        return points;
    }
}
