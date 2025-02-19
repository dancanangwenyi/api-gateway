package com.example.gateway.filter;

import com.example.gateway.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtConfig jwtConfig;

    public static class Config {
        // Configuration class, if needed
    }
    public JwtAuthenticationFilter(JwtConfig jwtConfig) {
        super(Config.class);
        this.jwtConfig = jwtConfig;

    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();

            // Use a direct check for the path with wildcard matching
            if (path.matches("/api/v1/auth/.*")) {
                // Allow the request to pass without filtering
                return chain.filter(exchange);
            }

            // Retrieve Authorization header
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorizedResponse(exchange, "Authorization header missing or invalid");
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix
            try {
                // Validate and extract claims
                Claims claims = extractAllClaims(token);

                // Add security details to request headers for downstream services
                exchange.getRequest().mutate()
                        .header("X-Authenticated-User", claims.getSubject())
                        .header("X-User-Roles", String.join(",", getAuthorities(claims)))
                        .header("X-Token-Issued-At", String.valueOf(claims.getIssuedAt().getTime()))
                        .build();

            } catch (Exception e) {
                return unauthorizedResponse(exchange, e.getMessage());
            }

            return chain.filter(exchange);
        };
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private List<String> getAuthorities(Claims claims) {
        // Fetch the authorities as a List<?> to avoid unchecked assignment
        List<?> authorities = claims.get("Authorities", List.class);

        // Optionally check if the list contains only strings
        if (authorities != null && !authorities.isEmpty() && authorities.get(0) instanceof String) {
            return (List<String>) authorities;  // Cast to List<String> after checking the type
        } else {
            return List.of();  // Return an empty list or handle error as needed
        }
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        byte[] bytes = String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\"}", message)
                .getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory()
                .wrap(bytes)));
    }
}
