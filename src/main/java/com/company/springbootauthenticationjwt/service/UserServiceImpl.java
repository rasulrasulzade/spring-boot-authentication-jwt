package com.company.springbootauthenticationjwt.service;

import com.company.springbootauthenticationjwt.entity.Role;
import com.company.springbootauthenticationjwt.entity.User;
import com.company.springbootauthenticationjwt.model.RegisterRequestModel;
import com.company.springbootauthenticationjwt.respository.RoleRepository;
import com.company.springbootauthenticationjwt.respository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createUser(RegisterRequestModel requestModel) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(requestModel.getEmail());
        user.setName(requestModel.getName());
        user.setSurname(requestModel.getSurname());
        user.setPassword(passwordEncoder.encode(requestModel.getPassword()));

        Role role = roleRepository.findByName("USER");
        user.addRole(role);

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user == null) throw new UsernameNotFoundException(username + " not found");

        return user;
    }
}
