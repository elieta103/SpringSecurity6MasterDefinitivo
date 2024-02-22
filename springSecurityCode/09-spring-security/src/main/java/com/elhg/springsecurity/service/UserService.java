package com.elhg.springsecurity.service;



import com.elhg.springsecurity.dto.SaveUser;
import com.elhg.springsecurity.persistence.entity.security.User;

import java.util.Optional;

public interface UserService {
    User registerOneCustomer(SaveUser newUser);

    Optional<User> findOneByUsername(String username);
}
