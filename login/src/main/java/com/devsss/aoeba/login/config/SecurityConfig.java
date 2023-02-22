package com.devsss.aoeba.login.config;

import com.devsss.aoeba.login.security.TokenServerSecurityContextRepository;
import com.devsss.aoeba.login.utils.JwtUtil;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


@Slf4j
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Value("${jwt.public.key}")
    private RSAPublicKey key;

    @Value("${jwt.private.key}")
    private RSAPrivateKey priv;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity security, TokenServerSecurityContextRepository tokenContextRepository
    ) {
        security.authorizeExchange()
                .pathMatchers("/login").permitAll()
                .anyExchange().authenticated()
                .and()
                // 禁用csrf
                .csrf().disable()
                .securityContextRepository(tokenContextRepository)
                .formLogin().disable()
                .httpBasic().disable();

        return security.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(this.key).build();
        JwtUtil.setJwtDecoder(jwtDecoder);
        return jwtDecoder;
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.key).privateKey(this.priv).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(jwks);
        JwtUtil.setJwtEncoder(jwtEncoder);
        return jwtEncoder;
    }

}
