package com.elhg.springsecurity.service.auth;


import com.elhg.springsecurity.dto.RegisteredUser;
import com.elhg.springsecurity.dto.SaveUser;
import com.elhg.springsecurity.dto.auth.AuthenticationRequest;
import com.elhg.springsecurity.dto.auth.AuthenticationResponse;
import com.elhg.springsecurity.persistence.entity.User;
import com.elhg.springsecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public RegisteredUser registerOneCustomer(SaveUser newUser) {
        User user = userService.registerOneCustomer(newUser);

        RegisteredUser userDto = new RegisteredUser();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());
        userDto.setRole(user.getRole().name());

        String jwt = jwtService.generateToken(user, generateExtraClaims(user));
        userDto.setJwt(jwt);

        return userDto;
    }

    private Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name",user.getName());
        extraClaims.put("role",user.getRole().name());
        extraClaims.put("authorities",user.getAuthorities());

        return extraClaims;
    }

    public AuthenticationResponse login(AuthenticationRequest autRequest) {
        //AuthenticationManager es el que tiene el metodo authenticate()
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                autRequest.getUsername(), autRequest.getPassword()
        );

        // El proveedor que resuelve la authenticacion es : http.authenticationProvider(daoAuthProvider),
        // Ver classes SecurityBeansInjector & HttpSecurityConfig
        authenticationManager.authenticate(authentication);
        // Hasta aqui el proceso de authenticacion fue exitoso.
        // Si no fue exitoso, lanza excepcion

        // Obtener detalles del usuario recien logeado
        UserDetails user = userService.findOneByUsername(autRequest.getUsername()).get();

        // Generar token
        String jwt = jwtService.generateToken(user, generateExtraClaims((User) user));

        AuthenticationResponse authRsp = new AuthenticationResponse();
        authRsp.setJwt(jwt);

        return authRsp;
    }

    /* Valida:
     1. Formato del token sea correcto(Header, Payload)
     2. Firma coincida
     3. No haya expirado en tiempo
     Extrayendo algun claims(atributo) del token, va realizar esas 3 validaciones.
   */
    public boolean validateToken(String jwt) {
        try{
            jwtService.extractUsername(jwt);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }

    }
}
