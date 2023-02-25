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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;


@Slf4j
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public KeyPair createKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器
        keyPairGen.initialize(1024, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        return keyPairGen.generateKeyPair();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity security, TokenServerSecurityContextRepository tokenContextRepository) {
        security.authorizeExchange()
                .pathMatchers("/login").permitAll()
                .pathMatchers(HttpMethod.GET).permitAll()
                .pathMatchers("/storage", "/wxgzh").authenticated()
                .anyExchange().authenticated()
                .and()
                // 禁用csrf
                .csrf().disable()
                // 在tokenContextRepository中进行身份认证
                .securityContextRepository(tokenContextRepository)
                .formLogin().disable()
                .httpBasic().disable();

        return security.build();
    }

    @Bean
    JwtDecoder jwtDecoder(KeyPair keyPair) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic()).build();
        // bean 生成后放进JwtUtil给静态方法调用
        JwtUtil.setJwtDecoder(jwtDecoder);
        return jwtDecoder;
    }

    @Bean
    JwtEncoder jwtEncoder(KeyPair keyPair) {
        JWK jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()).privateKey(keyPair.getPrivate()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(jwks);
        // bean 生成后放进JwtUtil给静态方法调用
        JwtUtil.setJwtEncoder(jwtEncoder);
        return jwtEncoder;
    }

}
