package com.greenfoxacademy.springwebapp.dtos;

public class UserDTO {
    private String username;
    private String email;
    private String password;
    private String kingdomname;

    public UserDTO() {
    }

    public UserDTO(String username, String email, String password, String kingdomname) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.kingdomname = kingdomname;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getKingdomname() {
        return kingdomname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setKingdomname(String kingdomname) {
        this.kingdomname = kingdomname;
    }
}