package com.company.springbootauthenticationjwt.controller;

import com.company.springbootauthenticationjwt.entity.User;
import com.company.springbootauthenticationjwt.model.RegisterRequestModel;
import com.company.springbootauthenticationjwt.model.RegisterResponseModel;
import com.company.springbootauthenticationjwt.service.UserService;
import com.company.springbootauthenticationjwt.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService service, JwtUtil jUtil){
        this.userService = service;
        this.jwtUtil = jUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseModel> register(@RequestBody RegisterRequestModel body){
//        TODO validate request payload

        if(userService.findByEmail(body.getEmail()) != null){
            throw new RuntimeException(body.getEmail() + " is already taken!");
        }

        User user = userService.createUser(body);

        RegisterResponseModel  responseModel = new RegisterResponseModel();
        responseModel.setId(user.getId());
        responseModel.setJwt(jwtUtil.createToken(user.getId()));

        return new ResponseEntity<>(responseModel, HttpStatus.CREATED);
    }
}
