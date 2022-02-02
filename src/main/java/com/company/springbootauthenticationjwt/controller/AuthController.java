package com.company.springbootauthenticationjwt.controller;

import com.company.springbootauthenticationjwt.entity.User;
import com.company.springbootauthenticationjwt.exception.CustomException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseModel> register(@RequestBody RegisterRequestModel body) {
        if (userService.findByEmail(body.getEmail()) != null) {
            throw new CustomException(body.getEmail() + " is already taken!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User user = userService.createUser(body);

        RegisterResponseModel responseModel = new RegisterResponseModel();
        responseModel.setId(user.getId());
        responseModel.setJwt("Bearer " + jwtUtil.generateToken(user));

        return new ResponseEntity<>(responseModel, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseModel> login(@RequestBody LoginRequestModel requestModel) {
        User foundUser = userService.findByEmail(requestModel.getEmail());

        if(foundUser == null )  throw new CustomException("Invalid email address", HttpStatus.INTERNAL_SERVER_ERROR);

        if(!passwordEncoder.matches(requestModel.getPassword(), foundUser.getPassword())) {
            throw new CustomException("Invalid password", HttpStatus.INTERNAL_SERVER_ERROR);
        }

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
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.generateToken(user))
                    .body(responseModel);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
}
