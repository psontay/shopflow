package com.shopflow.order.infrastructure.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Value("${JWT_SECRET_KEY}")
    private String jwtSecretKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer :: disable)
                   .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                   .authorizeHttpRequests(auth -> auth
                                                  .requestMatchers(HttpMethod.GET, "/api/v1/orders/**")
                                                  .authenticated()
                                                  .requestMatchers(HttpMethod.POST, "/api/v1/orders")
                                                  .hasRole("USER")
                                                  .anyRequest()
                                                  .authenticated()
                                         )
                   .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                       jwt.decoder(jwtDecoder());
                       jwt.jwtAuthenticationConverter(jwtAuthenticationConverter());
                   }))
                   .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        log.info("=========JWT SECRET KEY===========>>>>>" + jwtSecretKey);
        return NimbusJwtDecoder.withSecretKey(secretKey)
                               .macAlgorithm(MacAlgorithm.HS384)
                               .build();
    }

}
