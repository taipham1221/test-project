package com.taipham.test_project.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HomeController {
    
    @GetMapping
    public String helloWord() {
        return "Hello, Word!!!!!";
    }

    @GetMapping("/healthz")
    public String getHealthz() {
        return "Server is running!";
    }
    
    
}
