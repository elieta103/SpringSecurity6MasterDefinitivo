package com.elhg.springsecurity.persistence.entity;

import com.elhg.springsecurity.persistence.util.Role;
import com.elhg.springsecurity.persistence.util.RolePermission;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "\"user\"")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String name;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(role == null) return null;
        if(role.getPermissions() == null) return null;

        List<SimpleGrantedAuthority> authorities = role.getPermissions().stream()
                .map(item -> new SimpleGrantedAuthority(item.name()))
                .collect(Collectors.toList());
        // hasRole llama a hasAuthority pero le concatena al inicio ROLE_
        // Nos da como resultado ROLE_ADMINISTRADOR, por ende el SimpleGrantedAuthority se debe crear como: "ROLE_" + this.role
        // authorities": [{"authority": "READ_ALL_PRODUCTS"},{"authority": "READ_ONE_PRODUCT"},...,]
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
        // authorities": [{"authority": "READ_ALL_PRODUCTS"},{"authority": "READ_ONE_PRODUCT"},...,{"authority": "ROLE_ADMINISTRATOR"}]
        // Si no se agrega linea 42 no permite el acceso
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
