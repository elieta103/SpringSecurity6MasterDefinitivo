package com.elhg.springsecurity.controller;


import com.elhg.springsecurity.dto.RegisteredUser;
import com.elhg.springsecurity.dto.SaveUser;
import com.elhg.springsecurity.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private AuthenticationService authenticationService;

    @PreAuthorize("permitAll")
    @PostMapping
    public ResponseEntity<RegisteredUser> registerOne(@RequestBody @Valid SaveUser newUser){
        RegisteredUser registeredUser = authenticationService.registerOneCustomer(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PreAuthorize("denyAll")
    @GetMapping
    public String getMessage(){
        return "HOLA";
    }

}
