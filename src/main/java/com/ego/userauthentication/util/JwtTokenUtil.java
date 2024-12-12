package com.ego.userauthentication.util;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;
import java.util.logging.ErrorManager;

@Component
@Log4j2
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final long TOKEN_VALIDITY = 1000 * 60 * 60 * 10;

    private static final SignatureAlgorithm SIGNING_ALGORITHM = SignatureAlgorithm.HS512;

    @Value("${jwt.secret}")
    private String secret;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        try {
            return getUsernameFromToken(token);
        } catch (JwtException | IllegalArgumentException e) {
            // Log and handle the exception
            log.error("Failed to extract username from token", e);
            return null;
        }
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(currentDate());
    }

    private Date currentDate() {
        return new Date(System.currentTimeMillis());
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(currentDate())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(SIGNING_ALGORITHM, secret)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}