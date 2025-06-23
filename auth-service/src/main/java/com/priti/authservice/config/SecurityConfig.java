package com.priti.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll()) //It tells spring security to authorize all requests that it receives to the auth-service and let them through without adding any additional security checks. We dont need to block requests at the auth-service level as the only requests that we are going to receive are going to be from the API Gateway which we control and we also do not expose auth-service to the internet but that reduces the risk of receiving any bad request from any bad actor
                .csrf(AbstractHttpConfigurer::disable); //It disables cross site request forgery stuff. This is something we dont need as our traffic is coming from api gateway which we control and csrf is used to protect against  any frontend - client request that have a hacked authentication token. we dont need this because our auth service is secured by api gateway. It disables CSRF protection. CSRF is a type of attack that tricks the user into executing unwanted actions on a different site. We are not using CSRF protection in this case because we are not exposing our auth service to the internet. We are only allowing requests from the API Gateway which we control. So we can safely disable CSRF protection.

        return http.build(); // It will complete this filter chain and configure the spring security to be a bit less secure than we need it to be.
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder(); //It is a password encoder that uses the bcrypt hashing algorithm to hash passwords. It is a strong and secure way to hash passwords. It is used to hash the password before storing it in the database. It is also used to compare the hashed password with the plain text password when a user logs in.
    }

}
