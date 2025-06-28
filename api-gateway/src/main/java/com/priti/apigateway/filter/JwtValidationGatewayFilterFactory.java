package com.priti.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final WebClient webClient;

    public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,
                                             @Value("${auth.service.url}") String authServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }
    // The above constructor initializes the WebClient with the base URL of the authentication service.


    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> { //exchange variable is an object that gets passed to us by spring gateway that holds all the properties for current request, chain variable that manages the chain of filters that currently exist in filter chain
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION); // get the Authorization header from the request and we are assigning it to the token variable
            if (token == null || !token.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // if the token is null or does not start with "Bearer ", we set the response status to UNAUTHORIZED
                return exchange.getResponse().setComplete(); // and we complete the response. return this response to the client.
            }

            return webClient.get()
                    .uri("/validate") // we are making a GET request to the /validate endpoint of the authentication service
                    .header(HttpHeaders.AUTHORIZATION, token) // we are passing the token in the Authorization header
                    .retrieve() // we are retrieving the response from the authentication service
                    .toBodilessEntity() // we are converting the response to a bodiless entity, which means we are not interested in the body of the response
                    .then(chain.filter(exchange)); // if the response is successful, we continue with the request.
        };
    }
}
