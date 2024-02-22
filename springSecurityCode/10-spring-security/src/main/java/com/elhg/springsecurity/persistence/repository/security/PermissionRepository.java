package com.elhg.springsecurity.persistence.repository.security;

import com.elhg.springsecurity.persistence.entity.security.GrantedPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<GrantedPermission, Long> {
}
