package com.devsss.aoeba.login.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    JwtDecoder jwtDecoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.debug("开始执行JwtAuthenticationManager.authenticate");
        return Mono.just(authentication)
                .map(auth -> {
                            Jwt decode = jwtDecoder.decode((String) auth.getCredentials());
                            String claims = decode.getClaim("scope");
                            log.debug("用户名：" + decode.getSubject() + "  权限:" + claims);
                            return new UsernamePasswordAuthenticationToken(decode.getSubject(), null,
                                    Arrays.stream(claims.split(" ")).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                        }
                );
    }
}
