package com.example.playgame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String showWelcomePage() {
        return "index"; // This maps to /WEB-INF/jsp/index.jsp
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "authorization/login"; // This maps to /WEB-INF/jsp/authorization/login.jsp
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "authorization/register"; // This maps to /WEB-INF/jsp/authorization/register.jsp
    }
}
