package com.greenfoxacademy.springwebapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

@Entity
@Table(name = "players", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "user")
    @JsonManagedReference
    private Kingdom kingdom;

    private int points;

    private boolean verified;

    private String verificationToken;

    public User() {
    }

    public User(String username, String email, String password, String kingdomname) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.kingdom = new Kingdom(kingdomname, this);
        verified = false;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

    public void setKingdom(Kingdom kingdom) {
        this.kingdom = kingdom;
    }

    public int getPoints() {
        return points;
    }
    
    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
}
