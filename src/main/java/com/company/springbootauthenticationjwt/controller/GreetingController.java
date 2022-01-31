package com.company.springbootauthenticationjwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rest")
public class GreetingController {

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello World";
    }
}
