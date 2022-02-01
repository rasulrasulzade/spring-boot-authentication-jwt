package com.company.springbootauthenticationjwt.service;

import com.company.springbootauthenticationjwt.entity.User;
import com.company.springbootauthenticationjwt.model.RegisterRequestModel;

public interface UserService {
    User findByEmail(String email);

    User createUser(RegisterRequestModel requestModel);
}
