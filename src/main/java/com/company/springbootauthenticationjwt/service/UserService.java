package com.company.springbootauthenticationjwt.service;

import com.company.springbootauthenticationjwt.entity.User;
import com.company.springbootauthenticationjwt.model.RegisterRequestModel;
import com.company.springbootauthenticationjwt.respository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }


    public User createUser(RegisterRequestModel requestModel) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(requestModel.getEmail());
        user.setName(requestModel.getName());
        user.setSurname(requestModel.getSurname());
        user.setPassword(passwordEncoder.encode(requestModel.getPassword()));
       return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user == null) throw new UsernameNotFoundException(username + " not found");

        return user;
    }


}
