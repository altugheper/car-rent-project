package com.saferent.security.jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /*
    application.yml'a eklenen:
    saferent:
     app:
      jwtSecret: safeRent@!10
      jwtExpirationMs: 86400000
     */
    @Value("${saferent.app.jwtSecret}")
    private String jwtSecret;

    @Value("${saferent.app.jwtExpirationMs}")
    private Long jwtExpirationMs;


    //!!! Generate JWT token
    public String generateJwtToken(UserDetails userDetails){
        return Jwts.builder().
                setSubject(userDetails.getUsername()).
                setIssuedAt(new Date()).
                setExpiration(new Date(new Date().getTime() + jwtExpirationMs)). // 1 day expiration
                signWith(SignatureAlgorithm.HS512, jwtSecret).
                compact();
    }

    //!!! JWT token icinden email bilgisine ulasacagim method
    public String getEmailFromToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).
                parseClaimsJwt(token).
                getBody().
                getSubject();
    }


    //!!! JWT validate

}
