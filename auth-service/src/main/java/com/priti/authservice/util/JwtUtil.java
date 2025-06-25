package com.priti.authservice.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    // We use secret key to generate the JWT token and to prove that token has come from our servers and is valid anytime we receive this token in  subsequent requests.to our protected endpoints such as get medical profile.
    // We will initialize secret key

    private final Key secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) { // Injecting the secret key using environment variables.
        byte[] keyBytes = Base64.getDecoder().decode(secret.getBytes(
                StandardCharsets.UTF_8)); // We are decoding the secret key from base64 format to byte array
        this.secretKey = Keys.hmacShaKeyFor(keyBytes); // We have taken our secret key which is a string and we have converted it into a format which is a key object and then we can use this to create tokens
    }

    public String generateToken(String email, String role) {
        // Here we will generate the token using the secret key and the email and role of the user
        return Jwts.builder()
                .setSubject(email) //standard field
                .claim("role", role) //custom field
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) //10 hours. We are setting the expiration time of the token to 10 hours from now
                .signWith(secretKey)
                .compact(); // compact() method is going to take all above properties and create a string and it will sign that string using the secret key and return sined string that is our JWT token
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) secretKey)// This is how we verify a token is valid or not. JWT package will parse the token and verify its signature using the secret key.
                    .build() // build() method is used to create a parser instance
                    .parseSignedClaims(token); // parseSignedClaims(token) method will parse the token and throw an exception if the token is invalid or expired

        } catch (SignatureException e){
            throw new JwtException("Invalid JWT signature");
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT");
        }
    }
}
