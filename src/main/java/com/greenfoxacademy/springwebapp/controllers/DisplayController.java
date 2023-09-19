package com.greenfoxacademy.springwebapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DisplayController {
    @GetMapping(value = "/registration")
    public String displayRegistration() {
        return "registration";
    }

    @GetMapping("/")
    public String displayLogin() {
        return "login";
    }

    @GetMapping("/your-kingdom")
    public String displayIndex() {
        return "displayKingdom";
    }

    @GetMapping("/access-denied")
    public String displayAccessDenied() {
        return "accessDenied";
    }
}
