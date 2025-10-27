package com.zia.auth.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String helloGet() {
        return "Hello, World from GET!";
    }

    @PostMapping("/")
    public String helloPost() {
        return "Hello, World from POST!";
    }
}
