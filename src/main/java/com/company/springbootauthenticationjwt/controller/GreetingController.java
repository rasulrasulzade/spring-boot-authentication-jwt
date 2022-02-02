package com.company.springbootauthenticationjwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GreetingController {

    @GetMapping("/user")
    public String sayHelloFromUser(){
        return "Hello from User";
    }

    @GetMapping("/admin")
    public String sayHelloFromAmin(){
        return "Hello from Admin";
    }
}
