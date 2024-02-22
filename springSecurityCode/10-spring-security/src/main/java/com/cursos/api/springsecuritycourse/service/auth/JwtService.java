package com.cursos.api.springsecuritycourse.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.expiration-in-minutes}")
    private Long EXPIRATION_IN_MINUTES;

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;

    // Para la version 0.11.5
    /*public String generateToken(UserDetails user, Map<String, Object> extraClaims) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date( (EXPIRATION_IN_MINUTES * 60 * 1000) + issuedAt.getTime() );
        String jwt = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();

        return jwt;
    }*/

    // Para la version 0.12.3
    public String generateToken(UserDetails user, Map<String, Object> extraClaims) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date( (EXPIRATION_IN_MINUTES * 60 * 1000) + issuedAt.getTime() );
        String jwt = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(user.getUsername())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .claims(extraClaims)
                .signWith(generateKey(), Jwts.SIG.HS256)
                .compact();
        return jwt;
    }

    // 0.11.5
   /*private Key generateKey() {
        byte[] passwordDecoded = Decoders.BASE64.decode(SECRET_KEY);
        System.out.println( new String(passwordDecoded) );
        return Keys.hmacShaKeyFor(passwordDecoded);
    }*/

    // 0.12.3
    private SecretKey generateKey() {
        byte[] passwordDecoded = Decoders.BASE64.decode(SECRET_KEY);
        System.out.println( new String(passwordDecoded) );
        return Keys.hmacShaKeyFor(passwordDecoded);
    }



    public String extractUsername(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    // Para la version 0.11.5
    /*private Claims extractAllClaims(String jwt) {
        return Jwts.parserBuilder().setSigningKey( generateKey() ).build()
                .parseClaimsJws(jwt).getBody();
    }*/

    // Para la version 0.12.3
    private Claims extractAllClaims(String jwt) {
        return Jwts.parser().verifyWith(generateKey() ).build()
                .parseSignedClaims(jwt).getPayload();
    }


}