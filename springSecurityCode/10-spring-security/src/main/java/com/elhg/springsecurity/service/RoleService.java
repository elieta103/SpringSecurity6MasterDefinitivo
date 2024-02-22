package com.elhg.springsecurity.service;

import com.elhg.springsecurity.persistence.entity.security.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findDefaultRole();
}
