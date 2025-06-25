package com.priti.authservice.controller;

import com.priti.authservice.dto.LoginRequestDTO;
import com.priti.authservice.dto.LoginResponseDTO;
import com.priti.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Optional<String> tokenOptional = authService.authenticate(loginRequestDTO);
        if(tokenOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = tokenOptional.get();//it will convert tokenOptional from Optional<String> to an actual String
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @Operation(summary = "Validate Token")
    @GetMapping("/validate")
    public ResponseEntity<Void>  validateToken(@RequestHeader("Authorization") String authHeader) {
        // What is @RequestHeader("Authorization") String authHeader?
        // This tells Spring : When a request comes to this /validate endpoint, please look for a request header named Authorization and pass its value into the variable authHeader
        // What does the Authorization header look like?
        // Authorization: Bearer <your-jwt-token-here>
        // This format is standard (defined by RFC 6750) and widely used.
        // Why do we need this?
        // When the API Gateway receives a client request and wants to know: “Hey, is this JWT token real and valid?”
        // It will call this /validate endpoint, sending the token in the Authorization header like this:
        // GET /validate
        // Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
        // method will: Extract the token from the header, Validate it using your authService and JwtUtil class, Return a 200 OK if valid, Or a 401 Unauthorized if invalid or missing

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return authService.validateToken(authHeader.substring(7)) // substring(7) is used to remove the "Bearer " prefix from the token
                ? ResponseEntity.ok().build() // If the token is valid, return 200 OK
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // If the token is invalid, return 401 Unauthorized

    }

}
