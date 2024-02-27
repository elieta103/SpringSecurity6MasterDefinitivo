package com.elhg.springsecurity.controller;

import com.elhg.springsecurity.dto.LogoutResponse;
import com.elhg.springsecurity.dto.auth.AuthenticationRequest;
import com.elhg.springsecurity.dto.auth.AuthenticationResponse;
import com.elhg.springsecurity.persistence.entity.security.User;
import com.elhg.springsecurity.service.auth.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PreAuthorize("permitAll")
    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validate(@RequestParam String jwt){
        boolean isTokenValid = authenticationService.validateToken(jwt);
        return ResponseEntity.ok(isTokenValid);
    }

    @CrossOrigin
    @PreAuthorize("permitAll")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest authenticationRequest){

        AuthenticationResponse rsp = authenticationService.login(authenticationRequest);
        return ResponseEntity.ok(rsp);

    }

    @PreAuthorize("hasAuthority('READ_MY_PROFILE')")
    @GetMapping("/profile")
    public ResponseEntity<User> findMyProfile(){
        User user = authenticationService.findLoggedInUser();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout (HttpServletRequest request){
        authenticationService.logout(request);
        return  ResponseEntity.ok(new LogoutResponse("Logout exitoso"));
    }
}
