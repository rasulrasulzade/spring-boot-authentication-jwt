package com.company.springbootauthenticationjwt.controller;

import com.company.springbootauthenticationjwt.entity.User;
import com.company.springbootauthenticationjwt.exception.CustomException;
import com.company.springbootauthenticationjwt.model.LoginRequestModel;
import com.company.springbootauthenticationjwt.model.LoginResponseModel;
import com.company.springbootauthenticationjwt.model.RegisterRequestModel;
import com.company.springbootauthenticationjwt.service.UserService;
import com.company.springbootauthenticationjwt.util.HttpUtil;
import com.company.springbootauthenticationjwt.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final HttpUtil httpUtil;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UserService userService, PasswordEncoder passwordEncoder,HttpUtil httpUtil) {
        this.authenticationManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.httpUtil = httpUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseModel> register(@RequestBody RegisterRequestModel body) {
        if (userService.findByEmail(body.getEmail()) != null) {
            throw new CustomException(body.getEmail() + " is already taken!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User user = userService.createUser(body);

        LoginResponseModel responseModel = new LoginResponseModel();
        responseModel.setId(user.getId());
        responseModel.setEmail(user.getEmail());
        responseModel.setName(user.getName());
        responseModel.setSurname(user.getSurname());

        HttpHeaders httpHeaders = new HttpHeaders();

        Map<String,Object> map = new HashMap<>();
        map.put("email", user.getEmail());
        map.put("roles", user.getAuthorities());

        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.generateAccessToken(map));
        httpHeaders.add("X-refresh-token","Bearer " + jwtUtil.generateRefreshToken(map));

        return new ResponseEntity<>(responseModel, httpHeaders, HttpStatus.CREATED);
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

            Map<String,Object> map = new HashMap<>();
            map.put("email", user.getEmail());
            map.put("roles", user.getAuthorities());

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.generateAccessToken(map))
                    .header("X-refresh-token","Bearer " + jwtUtil.generateRefreshToken(map))
                    .body(responseModel);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader("X-refresh-token");
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            throw new RuntimeException("No header token");
        }

        String refreshToken = authorizationHeader.split("Bearer ")[1];

        Map<String,Object> map = jwtUtil.extractAllClaims(refreshToken);
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtil.generateAccessToken(map));

        httpUtil.printResponse(response, new HashMap<String, String>(), HttpServletResponse.SC_OK);
    }
}
