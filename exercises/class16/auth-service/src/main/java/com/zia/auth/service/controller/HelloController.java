package com.zia.auth.service.controller;

import com.zia.auth.service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String helloGet() {
        return "Hello, World from GET!";
    }

    @PostMapping("/role")
    public String createRole(@RequestBody String roleName) {
        return authService.addNewRole(roleName);
    }

    @PostMapping("/")
    public String helloPost() {
        return "Congratulations! You are authenticated successfully via POST!";
    }
}
