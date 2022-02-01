package com.company.springbootauthenticationjwt.controller;

import com.company.springbootauthenticationjwt.entity.User;
import com.company.springbootauthenticationjwt.model.LoginRequestModel;
import com.company.springbootauthenticationjwt.model.LoginResponseModel;
import com.company.springbootauthenticationjwt.model.RegisterRequestModel;
import com.company.springbootauthenticationjwt.model.RegisterResponseModel;
import com.company.springbootauthenticationjwt.service.UserService;
import com.company.springbootauthenticationjwt.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseModel> register(@RequestBody RegisterRequestModel body) {
//        TODO validate request payload

        if (userService.findByEmail(body.getEmail()) != null) {
            throw new RuntimeException(body.getEmail() + " is already taken!");
        }

        User user = userService.createUser(body);

        RegisterResponseModel responseModel = new RegisterResponseModel();
        responseModel.setId(user.getId());
        responseModel.setJwt("Bearer " + jwtUtil.createToken(user.getEmail()));

        return new ResponseEntity<>(responseModel, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseModel> login(@RequestBody LoginRequestModel requestModel) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(requestModel.getEmail(), requestModel.getPassword());
            Authentication auth = authenticationManager.authenticate(authToken);

            User user = (User) auth.getPrincipal();
            LoginResponseModel responseModel = new LoginResponseModel();
            responseModel.setId(user.getId());
            responseModel.setEmail(user.getEmail());
            responseModel.setName(user.getName());
            responseModel.setSurname(user.getSurname());

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.createToken(user.getEmail()))
                    .body(responseModel);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
}
