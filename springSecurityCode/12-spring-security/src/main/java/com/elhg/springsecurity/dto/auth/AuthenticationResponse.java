package com.elhg.springsecurity.dto.auth;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {
    private String jwt;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
